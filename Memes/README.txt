=== Memes(New Name Needed) ===
Coded by: Morgan Dock
Project: Semester
Class: CSci 582
Professor: Dr. Wilkins
Tested on: Nexus One emulator(secondary) and Cell Phone (Primarily; 2.3.6)

Description: This is my semester project that allows the creation of Images with captions, specifically targetting memes
and the sharing of those created images.

Functionality:
MemeEditActivity and MemeView are the primary activities. Between these two classes exits the code to save images, load images,
add captions, drag captions, share, and the rest of the core functionality.
Saving: Works 100% with the exception that it does not show up in the Gallery, but if you use File Manager
you can go to the folder and find them. I may need to adjust saving directory info the media folder of External Storage.
Sharing: Works Perfectly on my cell phone, buggy on the emulator with native messaging. On emulator, 90% of the time, the image with not
attach. Needs more testing and work or an actual device to test on.
Captions: Creates two Caption object into MemeView from the given strings.
Remove: Clears image and caption. 
Refresh: Clears Captions but not image
Chooser: Works, goes to a ListView where you select your image

Features:
Android Built in Activity with intent:
This is done via sharing and loading an image from the device
Action Bar:
Six options added to the Action bar
List View:
Selecting the chooser (the picture like image on the actionbar) will bring this up and so you can
select an image


Code:
Meme: The object used in the creation of Memelist. Pretty simple
Caption: The object used to create the text overlaid onto the Memeview bitmap
MemeEditActivity: The main core of functionality (currently)
MemeList: Image selection of ListView

MemeListAdapterWithCache
MemeListViewCache:
These two utilized example code and tutorials on fixing buggy and laggy listview.

MemeView:
This class utilizes much base code from old and outdated android developing example code, code from stack overflow
and code from google.  This would never have been completed without examples to draw from and even copy-paste & edit from.
 
 
Bugs:
If you share with no images loaded/made it will create a blank temp.jpg to send.

Final statement:
This has been the most enjoyable class i have had, and i look forward to senior project (hopefully with you as well)
 