# Medion
Dynamic Android Application which suggests restaurants in the vicinity of all the people in the group. [ Planned Meet-ups. ] 

## About
- Application randomly suggests restaurants according to server logic.
- Form groups from people in your contact list and send push notifications to other people in the formed group.
- Be a part of group your friend created by accepting the push notification.
- Schedule timed rendezvous with your friends.
- Request restaurants from servers and you can request another restaurant if not satisfied.

## How it is done developer instructions
- Client installs the application on device by downloading the APK from the website. 
- Client Login's by entering the credentials and information.
- Client Quickly creates a group by clicking hovering + button at bottom right corner
- Add People, Select name of group etc. and create group. (notifications are sent to the added people)
- After all the people accepted/reasonable people are there, you can ask server for restaurant. 
- Back End Server Algorithm takes the tentative locations of the group people and calculates median, picks a restaurant and returns JSON.
- Returns the data and the map location of the restaurant, name, ratings, reviews etc.
- If not satisfied, request a new restaurant. 

## Required Environment
> Android KitKat or higher SDK  
> Android OS on Mobile Device.  
> Allow App to access Contacts, Location.  
> Turn Off Screen Overlay.  

## Future Improvements
- Enhanced UI Design.
- Added Chat Functionality.
