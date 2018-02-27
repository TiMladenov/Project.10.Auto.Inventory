# Project 10: Auto Invertory
v2.0 of final Udacity ABND Project 10 - Invertory App

# Description
The goal of the final project 10 of Android Basics Nanodegree at Udacity is to design and create the structure of an Inventory App which would allow a store to keep track of its inventory of products. The app will need to store information about price, quantity available, supplier, and a picture of the product. It will also need to allow the user to track sales and shipments and make it easy for the user to order more from the listed supplier.

The tasks that needed to be completed for this project were:
- Storing information in an SQLite database.
- Integrating Android’s file storage systems into that database.
- Presenting information from files and SQLite databases to users.
- Updating information based on user input.
- Creating intents to other apps using stored information.

# Requirements
The design must include:
- Layout:
  - The app contains a list of current products and a button to add a new product.
  - Each list item displays the product name, current quantity, and price. Each list item also contains a Sale Button that reduces the quantity by one (include logic so that no negative quantities are displayed).
  - The Detail Layout for each item displays the remainder of the information stored in the database.
  - The Detail Layout contains buttons that increase and decrease the available quantity displayed.
  - The Detail Layout contains a button to order from the supplier.
  - The detail view contains a button to delete the product record entirely.
  - The code adheres to all of the following best practices:
    - Text sizes are defined in sp.
    - Lengths are defined in dp.
    - Padding and margin is used appropriately, such that the views are not crammed up against each other.
  - When there is no information to display in the database, the layout displays a TextView with instructions on how to populate the database. 
- Functionality:
  - The code runs without errors. For example, when user inputs product information (quantity, price, name, image), instead of erroring out, the app includes logic to validate that no null values are accepted. If a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue.
  - The listView populates with the current products stored in the table.
  - The Add product button prompts the user for information about the product and a picture, each of which are then properly stored in the table.
  - User input is validated. In particular, empty product information is not accepted. If user inputs product information (quantity, price, name, image), instead of erroring out, the app includes logic to validate that no null values are accepted. If a null value is inputted, add a Toast that prompts the user to input the correct information before they can continue. 
  - In the activity that displays a list of all available inventory, each List Item contains a Sale Button` which reduces the available quantity for that particular product by one (include logic so that no negative quantities are displayed).
  - Clicking on the rest of each list item sends the user to the detail screen for the correct product.
  - The Modify Quantity Buttons in the detail view properly increase and decrease the quantity available for the correct product. 
  - The student may also add input for how much to increase or decrease the quantity by.
  - The ‘order more’ button sends an intent to either a phone app or an email app to contact the supplier using the information stored in the database.
  - The delete button prompts the user for confirmation and, if confirmed, deletes the product record entirely and sends the user back to the main activity. 
  - The intent of this project is to give you practice writing raw Java code using the necessary classes provided by the Android framework; therefore, the use of external libraries for core functionality will not be permitted to complete this project.
- Code Readability:
  - All variables, methods, and resource IDs are descriptively named such that another developer reading the code can easily understand their function.
  - The code is properly formatted i.e. there are no unnecessary blank lines; there are no unused variables or methods; there is no commented out code. The code also has proper indentation when defining variables and methods.

# Screenshots
[Screenshot 1](https://drive.google.com/open?id=181jsZmYNzfxoywlCTCnpyEXMIJ0pSAJI) |
[Screenshot 2](https://drive.google.com/open?id=1TCtHUFt0_-JzYhBmU3uckdk0-CH01nvg) |
[Screenshot 3](https://drive.google.com/open?id=1ac2Dr4GoYBM88sA0DR9rQ7cM3WpbySax) |
[Screenshot 4](https://drive.google.com/open?id=1_ZVUJFAWCgZIRQS6ezjt44cZ7WTy8T1m) |
[Screenshot 5](https://drive.google.com/open?id=13XGgBjb8fPUzVu7fNY1cPzz2Uikd7sAT) |
[Screenshot 6](https://drive.google.com/open?id=1KKe-uWKxdlA8yoa-185kTyGZxmi_uSdU) |
[Screenshot 7](https://drive.google.com/open?id=1PXEUHTFb6nG6Fbyq_fp5duzZ6q6phcqz) |
[Screenshot 8](https://drive.google.com/open?id=1ctWcdjvW0R39-YEQQk-r3ifDSatZyTlj) |
[Screenshot 9](https://drive.google.com/open?id=1Ew6n7Q80MD0sjzjdNPfbetcQ1fzS-QI7) |
[Screenshot 10](https://drive.google.com/open?id=1uMF_NL90qB16S6GmDhqKAPANOXmyoBp0) |
[Screenshot 11](https://drive.google.com/open?id=1YfYzc31PNfglt18fdDPAgLVObJkPOu80) |
[Screenshot 12](https://drive.google.com/open?id=1RaShfHNdgyz-GVOLhEOiVlf4itNBFJFu)


# Video
- Video presentation of v2.0 of the application:

[![ScreenShot](https://i.ytimg.com/vi/fE5KG-AxU_U/hqdefault.jpg)](https://youtu.be/fE5KG-AxU_U)
