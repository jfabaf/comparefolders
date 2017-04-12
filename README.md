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
2/image4.jpg
```

And you also imagine that 2/fffg.jpg is the same file that 1/january/image1.jpg (MD5 is the same) and image2.jpg is the same in both folders.

comparefolders only says:

```
$java -jar comparefolders.jar -f1 ./1 -f2 ./2
+'1/may/image3.jpg' (which means file '1/may/image3.jpg' is not found in folder2)
-'2/image4.jpg' (which means file '2/image4.jpg' is not found in folder1)
```


And it says nothing about fffg.jpg and image2.jpg because these files are in folder 2 (same MD5)

You can use -htX and -fX together if you don't want to calculate same md5 files again.

Usage
-----

```
usage: comparefolders  [-d] [-f1 <folder1>] [-f1only] [-f2 <folder2>]
       [-hs1 <file>] [-hs2 <file>] [-ht1 <file>] [-ht2 <file>] [-sp]
 -d,--debug                     Show debug info
 -f1,--folder1 <folder1>        Path to folder 1
 -f1only,--f1only               Only process files in f1 not found in f2
                                but not viceversa
 -f2,--folder2 <folder2>        Path to folder 2
 -hs1,--hashtablesave1 <file>   save hashtable of folder1 to file
 -hs2,--hashtablesave2 <file>   save hashtable of folder2 to file
 -ht1,--hashtable1 <file>       Use hashtable1 file as a folder1
 -ht2,--hashtable2 <file>       Use hashtable2 file as a folder2
 -sp,--showprogress             Show percentaje of progress
 ```