# KeepAnEye
This is the final project for the udacity&Google 1-year Android training.

The app described in Project 7 (https://github.com/kureda/Capstone-Project/blob/master/Capstone_Stage1.pdf)
The idea is to monitor your relatives. If you granny's not using her phone for a while or not walking/riding
with it for a while, your phone warns you: the widget (picture of old lady) turnes yellow and then red.

What's different:

1. The used 3-rd pary library is not synergy, but OKHttp

2. The used Google service is not Firebase Messaging, but Firebase analytics

3. Sleep-wake time feature. You can set go-to-bed and wake time, but they are just ignored.

For the app you need passwords for google services etc, I'll send reference to them separately.
To test the app, you need at least two smarthones.

1. Install it in the 'carer' mode. It'll give you an id

2. Drag widget to the screen. It should be transparent picture of old lady.

2. Install it to other smartphone, in 'cared' mode, using id from step one.

3. Wait till the cared phone uploads report. (at real life it would be an hour, but for testing I set it to 2 minutes)

4. Wait till the carer phone downloads the report. Since miminum allowed by Google is 1 hr, you better do it manually,
by touching the sync icon on the toolbar.

5. Cared phone information will appear. You can go to settings, change details etc. Depending on settings and the report
from your 'cared' smartphone, widget with the old lady will  turn yellow or green.

6. You can call from phone to phone by pressing corresponding buttons. 

7. Cared phone should keep sending reports even after reboot.

8. Change KeepAnEyeId in 'carer' phone to 62276370. You will see three more 'cared' phones (those are mine). Old ones still would be displayed, but their state won't be updated. I don'd delete database records, just hide them (by switch in settings).

That's about it. Enjoy. :)
