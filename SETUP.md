
# Set up WPAndroidTemplate

# Requirement
 
 1. [WordPress CMS] (https://wordpress.com/).
 2. [WP REST API](http://v2.wp-api.org/).
 
# Configuration
 
 1. Create a flavor for your app [Configuring Gradle Builds](http://developer.android.com/tools/building/configuring-gradle.html).
 2. Copy paste the value folder from the main resources (src/main/res/value) into your flavor resources and then overide relevant resources like: the **app name**, the **baseUrl**, **fb_linl**, **category_name**...
 
 ``` PS: there are 2 flavors in this project (preferably use lwn_magazine flavor as it the most robust) and they can be used as a straight forward example. ```
 
# Set up GCM

 1. Get this Plug in [Click me](http://codecanyon.net/item/wp-google-cloud-messaging/9942568).
 2. Upload the folder 'wp-gcm' to the '/wp-content/plugins' directory on your server.
 3. Go to 'Plugins' trough the admin dashboard and activate the plugin.
 4. After the activation you will be redirected to the settings page where you can setup the plugin. You need your Api-key.
 5. Follow this to get your apiKey  [Click me](https://developers.google.com/cloud-messaging/).
 6. Set your project id (project id from where you got the key from) in the app **project_id** ressource (found in src/main/res/value/string.xml).
 7. When everything is setted up click save, and your finished!, Now you can write Messages.
 
 
 
