spritely
========

Spritely is a tool for autogenerating simple placeholder game sprites from images found on the web. To see some example images, click here: www.gamesbyangelina.org/downloads/spritely/examples/spritelyexamples.png

If you use Spritely for anything, from placeholders to a new genre of games, let me know! You don’t have to, of course, but it will put a smile on my face and also let me justify spending time on making it. I want to create more tools like this that spin off from my day job (where I research techniques for automatic game design). If I get lots of feedback I can show this off to people next time the funding nightmare comes back.

Usage
=====

You can interact with Spritely in three ways. First, you can run it as a .jar file and use my fairly rubbish UI. For a quick download of this version go here: www.gamesbyangelina.org/downloads/spritely/download.php

Secondly, you can run it from the command line. This gives you access to a few features I've not added to the UI yet (like setting the size of the output images) and also means you can more easily integrate it into projects not written in Java, for use automatically. www.gamesbyangelina.org/downloads/spritely/download-cli.php

Here's an example call:

`java -jar spritely-cli.jar -sgi -d 16 -n 1 -c winter wolf`

Thirdly, you can simply bung the source code into your Java project, instantiate a Spritely() object, and use it in-code.

I'm doing my best to maintain the project, but bear in mind that this doesn't relate directly to my day job, and I'm new to this whole releasing-tools-for-people schtick. I've tried to include some use cases so you can see what everything does, but although the code isn't commented I think Spritely is a fairly simple library. Picking it apart will probably tell you all you need to know, and this code is public domain if you want to improve it or add features!

License
=======

Licenses - Spritely
--------

"There's no limit to what a person might accomplish if they don't mind who makes a profit."

Spritely's code is effectively **public domain**. This means that as the author of the code, I do not claim any rights to it whatsoever. You may use it for commercial or non-commercial use, you may chop up the source code, redistribute it, show it to your friend and pretend you wrote it, use it as placeholder code when shooting a movie about a computer programmer, and so on. To ensure everyone is happy, I have licensed the code under the DO WHAT THE FUDGE YOU WANT license, which permits you to do whatever the fudge you want with the code. Go nuts!

The license does not extend to the libraries that Spritely uses, however. See below.

Licenses - Libraries
---------

As of version 0.1, Spritely depends on three libraries:

**Google GSON 2.1** - http://code.google.com/p/google-gson/

**Apache Commons CLI** - http://commons.apache.org/cli/

**Tag Soup** - http://ccil.org/~cowan/XML/tagsoup/

All three of these libraries are licensed under the Apache 2.0 license. This means - I *think* - that in order to distribute the libraries, as I am doing, I merely need to include a copy of the license file. I have done so in the /libs folder, but please let me know if I am making a mistake. In any case, feel free to use just the code and download your own copy of the libs from the sources above.

Licenses - Output
---------

Spritely generates sprites by crawling the web for images. This is fraught with potential problems, and even though I happily use it for personal and research projects, I unfortunately cannot promise to protect you from legal responsibility, etc. etc. You use Spritely at your own risk.

That said, I am working on more features that will let you use it with confidence in the future. For now, you can try using flags such as `-swc` and `-soc` to restrict searches to Wikimedia Commons and OpenClipart sites only, removing Google Images from the search. I'll add in Creative Commons search to the Google options at a later date.

For small projects or prototyping, I imagine you will be just fine, but *I am not a lawyer*. I cannot stress this enough. Anyone who has met me or asked me to defend them in court will attest to this. 