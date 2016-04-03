# RoadTrip
A Road Safety App. Make-It-Count Hackathon
Team Name 2020B

Our solution revolves around prevention of accidents by incentivising good driving. We plan to achieve it by an android app which will have the following features,


 + Notification when the user is going faster than the approved speed limit in that region (GCM and google maps roads API)

 + Location based notifications when the user is in accident prone areas (accomplished through Location and Accelerometer API)

 + The user will be required to place the mobile such that we are able to capture the view of the road with it. Through the camera feed we will get to know whether the user exhibited good or bad driving. We will open this feed to the public and crowdsource the vetting process.

#Instructions to run
- App opens with a screen, showing live video of driving which is a camera feed. Camera feed can be upvoted/downvoted by the viewers. 
- Driver details will be shown, along with average speed. As soon as it increases above 45 kmph, a toast is shown, saying you are exceeding safe speed limit, a Push Notification is received, which can be sent from a server. Here is the link of demo server to test it http://www.androidhive.info/2016/02/android-push-notifications-using-gcm-php-mysql-realtime-chat-app-part-2/. Use API key: AIzaSyBhlG2OZ2EtJ1_PQ1qf-v_jVMusbvuB3Z0
and Registration Token: eZA9MAC2OIk:APA91bG8LpNOs3llNn_4BSX6ebwpIaauPLvTYuuc54z029mA1jHE0-d2wN7NlMdxLjyVwWpTYvKFheTI7El0BIMFMVNmJ5axiJyvwFhlWRZ1eyFyGjOmcXWZ_S7fr7N5yy1eOGxdZ4hf
- We are providing dummy data of crashes to the user, click on add a new Proximity Alert button, which will set a crash location co-oridinate, when a user is within a particular distance to that location, a push notification is sent giving the last crash details and warning user to drive slow.




