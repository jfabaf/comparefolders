comparefolders
==============

Compare the content of two folders.
It compares the contents of the files, It doesn't matter the path or the name of the files.

Imagine that you have these files:

Folder1:
1/january/image1.jpg
1/may/image2.jpg
1/may/image3.jpg

Folder2:
2/fffg.jpg
2/image2.jpg

And you also imagine that 2/fffg.jpg is the same file that 1/january/image1.jpg (MD5 is the same).

comparefolders only says:
$java -jar comparefolders.jar -f1 ./1 -f2 ./2
...
File '1/may/image3.jpg' not found in folder2

And it says nothing about fffg.jpg and image2.jpg because these files are in folder 2 (same MD5)