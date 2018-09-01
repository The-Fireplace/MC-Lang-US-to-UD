To add this to your gradle project:
Between `buildscript{...}` and `apply plugin: 'net.minecraftforge.gradle.forge'`, insert the following:

`plugins{
    id "thefireplace.en2ud" version "1.0.3"
}`

Anywhere after the plugins are applied, add the following:
`en2ud {
     modid = "YOUR_MODID_GOES_HERE"
 }`
This section tells it which folder to look for the lang files in, assuming the default directory structure of `src/main/resources/assets/YOUR_MODID_GOES_HERE/lang`

Inside `en2ud{...}`, there are also several optional parameters you may set:

`customRegex` can be set to a regex String that should not be turned upside down. If your mod uses something in the lang files like @VERSION@, or @1, @2, @3, etc, you should set this to regex that covers that to prevent it from being turned backwards and upside down.

`copyInvalidLines` can be set to a boolean that specifies if you want to copy invalid lines to the generated file. If you write comments saying what each section is, you might want to set this to true. Default setting is false.

`debug` can be set to a boolean that, when true, makes the console output more verbose. This is useful if you are having issues getting this to generate the file. Default setting is false.
 
To run the script, run `gradlew en2ud`
 
And that's all there is to it! The output from running it will let you know if anything went wrong.
 
This supports vanilla color codes (§1, §f, etc) and variables inserted with %s, %d, etc.