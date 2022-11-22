# Auto Video Editor

This Project allows you to sepcify a main video, and other subvideos with their display timestamps, so you can sit
back and watch it merge your videos!!!

It uses Xuggler, which is an FFMPEG Java wrapper.

# GUI
![gui2](https://user-images.githubusercontent.com/47413908/203424020-a9dda708-ea50-456d-890f-306ad8a47f32.PNG)


# Steps to edit your videos: 
<ul>
<li>Specifiy a path for you main video (or click MainOk! without adding a path to use a default video in this case the default video is: resources/jp_t.mp4) and then press MainOk! which is the video below</li>


https://user-images.githubusercontent.com/47413908/203421090-bcc5b06b-33a2-4d84-b707-80724bee6480.mp4


<li>Specify paths separated by commas for sub videos you want to place as cutscenes in specific timestamps of your choice (or click SubsOk! without adding paths to use default videos which are: resources/island.mp4,resources/ocean.mp4) and then press SUbsOK! which are the videos below </li>


https://user-images.githubusercontent.com/47413908/203421145-019af20b-dd85-40c4-adea-487c959e2e6d.mp4


https://user-images.githubusercontent.com/47413908/203421160-27fbc33c-9538-4bad-a6b8-df5cded42597.mp4



<li>Specify the timestamps separated by commas and then press MarkersOk</li>
<li>Specify output path (or press OutOk! without specifying an output path which would use the following default path: resources/out.mp4) and then press OutOk!</li>

</ul>

# Example
Lets say we want to create the next motivational video for Jordan peterson, and we want to insert in certain timestamps to make a good motivational video transitions:

![video1989010209_trimmed(1)](https://user-images.githubusercontent.com/47413908/203420370-b703612c-9d00-4cf9-a0a7-0a4f14e22537.gif)

The timestamps I used are at: 3 and 12, which will basically insert the first video at second number 3 and the second video at second number 12

### Output





https://user-images.githubusercontent.com/47413908/203423332-fd825361-2d64-4e8a-b068-1610af037500.mp4

we can see the videos are placed in the timestamps provided (3 and 12).



