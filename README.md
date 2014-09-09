comparefolders
==============

Compare the content of two folders.
It compares the contents of the files, It doesn't matter the path or the name of the files.

Imagine that you have these files:

```
Folder1:
1/january/image1.jpg
1/may/image2.jpg
1/may/image3.jpg
```

```
Folder2:
2/fffg.jpg
2/image2.jpg
```

And you also imagine that 2/fffg.jpg is the same file that 1/january/image1.jpg (MD5 is the same).

comparefolders only says:

```
$java -jar comparefolders.jar -f1 ./1 -f2 ./2
...
File '1/may/image3.jpg' not found in folder2
```

And it says nothing about fffg.jpg and image2.jpg because these files are in folder 2 (same MD5)

Usage
-----

```
usage: comparefolders  [-f1 <folder1>] [-f2 <folder2>] [-hs1 <file>] [-hs2
       <file>] [-ht1 <file>] [-ht2 <file>] [-sp]
 -f1,--folder1 <folder1>        Path to folder 1
 -f2,--folder2 <folder2>        Path to folder 2
 -hs1,--hashtablesave1 <file>   save hashtable of folder1 to file
 -hs2,--hashtablesave2 <file>   save hashtable of folder2 to file
 -ht1,--hashtable1 <file>       Use hashtable1 file as a folder1
 -ht2,--hashtable2 <file>       Use hashtable2 file as a folder2
 -sp,--showprogress             Show percentaje of progress
 ```