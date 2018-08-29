To add this to your gradle project:
Between `buildscript{...}` and `apply plugin: 'net.minecraftforge.gradle.forge'`, insert the following:

`plugins{
    id "thefireplace.en2ud" version "1.0.1"
}`

Anywhere after the plugins are applied, add the following:
`en2ud {
     modid = "YOUR_MODID_GOES_HERE"
 }`
This section tells it which folder to look for the lang files in, assuming the default directory structure of `src/main/resources/assets/YOUR_MODID_GOES_HERE/lang`
 
To run the script, run `gradlew en2ud`
 
And that's all there is to it! The output from running it will let you know if anything went wrong.
 
This supports vanilla color codes (§1, §f, etc) and variables inserted with %s, %d, etc.