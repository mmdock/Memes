/*
 * @author Morgan Dock
 * 
 * Resources used: Android developer site, Stackoverflow, Bitmapping example apps provided by google and 
 * various other sources.  This is FrakenCode (get it? FrakenCode?...) 
 * Used these examples to piece it all together. Would NEVER have figured out much of this on my own (in the time allotted)
 */

package edu.olemiss.mmdock.memes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MemeView extends ImageView {

    
    private static final float SCALED_IMAGE_MAX_DIMENSION = 500f;

    // Other standard meme image parameters
    private static final int FONT_SIZE = 44;

    private Bitmap scaledBitmap;  // The photo picked by the user, scaled
    private Bitmap workingBitmap;  // The Bitmap we render the caption text into
    private final Caption[] captions = new Caption[] { new Caption(), new Caption() };

    // State used while dragging a caption around
    private boolean drag;
    private int dragCaptionIndex;  // index of the caption (in captions[]) that's being dragged
    private int touchDownX, touchDownY;
    private final Rect initialDragBox = new Rect();
    private final Rect currentDragBox = new Rect();
    private final RectF currentDragBoxF = new RectF();  // used in onDraw()
    private final RectF transformedDragBoxF = new RectF();  // used in onDraw()
    private final Rect tmpRect = new Rect();

    public MemeView(Context context) {
        super(context);
    }

    public MemeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MemeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public Bitmap getWorkingBitmap() {
        return workingBitmap;
    }

    public String getTopCaption() {
        return captions[0].caption;
    }

    public String getBottomCaption() {
        return captions[1].caption;
    }

    public boolean hasValidCaption() {
        return !TextUtils.isEmpty(captions[0].caption)
                || !TextUtils.isEmpty(captions[1].caption);
    }

    public void clear() {
        scaledBitmap = null;
        workingBitmap = null;
        setImageDrawable(null);
    }

    /**
     * @param uri
     * Load Specified uri directly
     * TODO:Rather than calling setImageURI() with the URI of
     * the (normal sized) photo, it would be better to turn the URI into
     * a scaled Bitmap right here, and load *that*
     */
    public void loadFromUri(Uri uri) {
        setImageURI(uri);
        BitmapDrawable drawable = (BitmapDrawable) getDrawable();

        Bitmap fullSizeBitmap = drawable.getBitmap();

        Bitmap.Config config = fullSizeBitmap.getConfig();

        float origWidth = fullSizeBitmap.getWidth();
        float origHeight = fullSizeBitmap.getHeight();
        float aspect = origWidth / origHeight;

        float scaleFactor = ((aspect > 1.0) ? origWidth : origHeight) / SCALED_IMAGE_MAX_DIMENSION;
        int scaledWidth = Math.round(origWidth / scaleFactor);
        int scaledHeight = Math.round(origHeight / scaleFactor);

        scaledBitmap = Bitmap.createScaledBitmap(fullSizeBitmap,
                                                  scaledWidth,
                                                  scaledHeight,
                                                  true);
        
    }

    /**
     * Sets the captions for this memeView.
     * TODO: Right now, captions size is hard coded.  
     */
    public void setCaptions(String topCaption, String bottomCaption) {
        if (topCaption == null) topCaption = "";
        if (bottomCaption == null) bottomCaption = "";

        captions[0].caption = topCaption;
        captions[1].caption = bottomCaption;
        if (TextUtils.isEmpty(captions[0].caption)) {
            captions[0].positionValid = false;
        }
        if (TextUtils.isEmpty(captions[1].caption)) {
            captions[1].positionValid = false;
        }
        captions[0].captionBoundingBox = null;
        captions[1].captionBoundingBox = null;

        renderCaptions(captions);
    }

    /**
     * Clears the captions for this memeView.
     */
    public void clearCaptions() {
        setCaptions("", "");
    }

    /**
     * Renders this memeView's current image captions into our
     * underlying ImageView.
     */
    public void renderCaptions(Caption[] captions) {
        // TODO: dynamically handle an array, rather than
        // assuming "top" and "bottom" captions.

        String topString = captions[0].caption;
        boolean topStringValid = !TextUtils.isEmpty(topString);

        String bottomString = captions[1].caption;
        boolean bottomStringValid = !TextUtils.isEmpty(bottomString);

        if (scaledBitmap == null) return;

        Bitmap.Config config = scaledBitmap.getConfig();

        workingBitmap = scaledBitmap.copy(config, true /* isMutable */);

        Canvas canvas = new Canvas(workingBitmap);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(FONT_SIZE);
        textPaint.setColor(0xFFFFFFFF);

        Typeface face = textPaint.getTypeface();
        face = Typeface.DEFAULT_BOLD;
        textPaint.setTypeface(face);

        final int edgeBorder = 20;
        final int fontHeight = textPaint.getFontMetricsInt(null);
        int topX = 0;
        int topY = 0;
        if (topStringValid) {
            if (captions[0].positionValid) {
                topX = captions[0].xpos;
                topY = captions[0].ypos;
            } else {
                topX = edgeBorder;
                topY = edgeBorder + (fontHeight * 3 / 4);
                captions[0].setPosition(topX, topY);
            }
        }

        int bottomX = 0;
        int bottomY = 0;
        if (bottomStringValid) {
            if (captions[1].positionValid) {
                bottomX = captions[1].xpos;
                bottomY = captions[1].ypos;

            } else {
                final int bottomTextWidth = (int) textPaint.measureText(bottomString);
                bottomX = canvas.getWidth() - edgeBorder - bottomTextWidth;
                bottomY = canvas.getHeight() - edgeBorder;
                captions[1].setPosition(bottomX, bottomY);
            }
        }

        // Finally, render the text.
        // (TODO: This is a hack, and still doesn't look as good as the normal meme style of text

        final float shadowRadius = 2.0f;
        final int shadowOffset = 2;
        final int shadowColor = 0xff000000;

        textPaint.setShadowLayer(shadowRadius, shadowOffset, shadowOffset, shadowColor);
        if (topStringValid) canvas.drawText(topString, topX, topY, textPaint);
        if (bottomStringValid) canvas.drawText(bottomString, bottomX, bottomY, textPaint);
        //
        textPaint.setShadowLayer(shadowRadius, -shadowOffset, shadowOffset, shadowColor);
        if (topStringValid) canvas.drawText(topString, topX, topY, textPaint);
        if (bottomStringValid) canvas.drawText(bottomString, bottomX, bottomY, textPaint);
        //
        textPaint.setShadowLayer(shadowRadius, shadowOffset, -shadowOffset, shadowColor);
        if (topStringValid) canvas.drawText(topString, topX, topY, textPaint);
        if (bottomStringValid) canvas.drawText(bottomString, bottomX, bottomY, textPaint);
        //
        textPaint.setShadowLayer(shadowRadius, -shadowOffset, -shadowOffset, shadowColor);
        if (topStringValid) canvas.drawText(topString, topX, topY, textPaint);
        if (bottomStringValid) canvas.drawText(bottomString, bottomX, bottomY, textPaint);


        int textWidth, textHeight;

        if (topStringValid && captions[0].captionBoundingBox == null) {
            textPaint.getTextBounds(topString, 0, topString.length(), tmpRect);
            textWidth = tmpRect.width();
            textHeight = tmpRect.height();
            captions[0].captionBoundingBox = new Rect(topX, topY - textHeight,
                                                       topX + textWidth, topY);
        }
        if (bottomStringValid && captions[1].captionBoundingBox == null) {
            textPaint.getTextBounds(bottomString, 0, bottomString.length(), tmpRect);
            textWidth = tmpRect.width();
            textHeight = tmpRect.height();
            captions[1].captionBoundingBox = new Rect(bottomX, bottomY - textHeight,
                                                       bottomX + textWidth, bottomY);
        }

        // Finally, display the new Bitmap to the user:
        setImageBitmap(workingBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (drag) {

            Matrix m = getImageMatrix();

            currentDragBoxF.set(currentDragBox);
            m.mapRect(transformedDragBoxF, currentDragBoxF);
            transformedDragBoxF.offset(getPaddingLeft(), getPaddingTop());

            Paint p = new Paint();
            p.setColor(0xFFFFFFFF);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(2f);

            canvas.drawRect(transformedDragBoxF, p);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        Matrix m = getImageMatrix();

        Matrix invertedMatrix = new Matrix();
        m.invert(invertedMatrix);

        float[] pointArray = new float[] { ev.getX() - getPaddingLeft(),
                                           ev.getY() - getPaddingTop() };

        invertedMatrix.mapPoints(pointArray);

        int eventX = (int) pointArray[0];
        int eventY = (int) pointArray[1];

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (drag) {
                    drag = false;  
                }
                if (!hasValidCaption()) {
                    return true;
                }

                // See if this DOWN event hit one of the caption bounding
                // boxes.  If so, start dragging!
                for (int i = 0; i < captions.length; i++) {
                    Rect boundingBox = captions[i].captionBoundingBox;


                    if (boundingBox != null) {
                        tmpRect.set(boundingBox);

                        final int touchPositionSlop = 40;  // pixels
                        tmpRect.inset(-touchPositionSlop, -touchPositionSlop);

                        if (tmpRect.contains(eventX, eventY)) {

                            drag = true;
                            dragCaptionIndex = i;
                            break;
                        }
                    }
                }
                if (!drag) {

                    return true;
                }

                touchDownX = eventX;
                touchDownY = eventY;

                initialDragBox.set(captions[dragCaptionIndex].captionBoundingBox);
                currentDragBox.set(captions[dragCaptionIndex].captionBoundingBox);

                invalidate();

                return true;

            case MotionEvent.ACTION_MOVE:
                if (!drag) {
                    return true;
                }

                int displacementX = eventX - touchDownX;
                int displacementY = eventY - touchDownY;

                currentDragBox.set(initialDragBox);
                currentDragBox.offset(displacementX, displacementY);

                invalidate();

                return true;

            case MotionEvent.ACTION_UP:
                if (!drag) {
                    return true;
                }

                drag = false;

                int offsetX = eventX - touchDownX;
                int offsetY = eventY - touchDownY;
                captions[dragCaptionIndex].xpos += offsetX;
                captions[dragCaptionIndex].ypos += offsetY;
                captions[dragCaptionIndex].captionBoundingBox = null;

                // Finally, refresh the screen.
                renderCaptions(captions);
                return true;

            // This case isn't expected to happen.
            case MotionEvent.ACTION_CANCEL:
                if (!drag) {
                    return true;
                }

                drag = false;
                // Refresh the screen.
                renderCaptions(captions);
                return true;

            default:
                return super.onTouchEvent(ev);
        }
    }

    /**
     * Returns an array containing the xpos/ypos of each Caption in our
     * array of captions.  (This method and setCaptionPositions() are used
     * by memeActivity to save and restore the activity state across
     * orientation changes.)
     */
    public int[] getCaptionPositions() {
        // TODO: captions currently has a hardcoded length of 2 (for
        // "top" and "bottom" captions).
        int[] captionPositions = new int[4];

        if (captions[0].positionValid) {
            captionPositions[0] = captions[0].xpos;
            captionPositions[1] = captions[0].ypos;
        } else {
            captionPositions[0] = -1;
            captionPositions[1] = -1;
        }

        if (captions[1].positionValid) {
            captionPositions[2] = captions[1].xpos;
            captionPositions[3] = captions[1].ypos;
        } else {
            captionPositions[2] = -1;
            captionPositions[3] = -1;
        }
        return captionPositions;
    }

    /**
     * Sets the xpos and ypos values of each Caption in our array based on
     * the specified values.  (This method and getCaptionPositions() are
     * used by memeActivity to save and restore the activity state
     * across orientation changes.)
     */
    public void setCaptionPositions(int[] captionPositions) {
        // TODO: captions currently has a hardcoded length of 2 (for
        // "top" and "bottom" captions).

        if (captionPositions[0] < 0) {
            captions[0].positionValid = false;
        } else {
            captions[0].setPosition(captionPositions[0], captionPositions[1]);
        }

        if (captionPositions[2] < 0) {
            captions[1].positionValid = false;
        } else {
            captions[1].setPosition(captionPositions[2], captionPositions[3]);
        }

        // Finally, refresh the screen.
        renderCaptions(captions);
    }
}
