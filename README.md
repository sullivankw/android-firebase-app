# android-firebase-app

* This uses Firebase as a real time database and storage. A user can view a list of profiles and add to that list   
* while getting updates to the profile list if changes are made to the profile list at the server or via other users

# TODOS 

* Move text from the xml files to strings.xml  
* Handle the multiple event listeners for the differing queries. If a change was made at the server and the user was
* viewing a sorted and/or filtered list that wouldn't include the made change in that sorted and/or filtered query
* then the onDataChange callback is triggered and we are forced back to viewing the default list
* Could handle image upload failures/resumes better
