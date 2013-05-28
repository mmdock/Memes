package edu.olemiss.mmdock.memes;
/*
 * Class used for captions on the image
 * Utilizing StackOverflow example for bounding box idea
 */
import android.graphics.Rect;
/**
 * Structure used to hold the entire state of a single caption.
 */
 public class Caption {
        public String caption;
        public Rect captionBoundingBox;  // updated by renderCaptions()
        public int xpos, ypos;
        public boolean positionValid;

        public void setPosition(int x, int y) {
            positionValid = true;
            xpos = x;
            ypos = y;
            // Also blow away the cached bounding box, to make sure it'll
            // get recomputed in renderCaptions().
            captionBoundingBox = null;
        }

        @Override
        public String toString() {
            return "Caption['" + caption + "'; bbox " + captionBoundingBox
                    + "; pos " + xpos + ", " + ypos + "; posValid = " + positionValid + "]";
        }
    }