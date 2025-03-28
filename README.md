# Blesbook

**Research Seminar "Basics of Android Development" | Project**

Author: Alexander Vasyukov

Last version: 1.1 (27.03.25)

## Description of project

This project is an Android app for managing of books and quotes, written in Kotlin.

![screens](screens.png)

### Home

- User Information: First name, last name, number of books read, and number of books in the wishlist
- Settings Button: Edit the user's name, and export or import data using JSON format
- Find Quote Button
- Find Book or Author Button
- News List

### Books

This screen displays a list of books read by the user.

Each item includes:
- Title
- Author
- Country
- Year
- Number of quotes

Long-pressing an item opens a dialog window, allowing the user to delete or edit the book.

### Wishlist

Similar to the Books screen, but it contains books the user wants to read. There is also there is a button to mark that a book is read.

### Add Book

A screen for adding new books.

It includes 4 fields and a status selection:
- Title
- Author
- Country
- Year
- Status: Read / Wishlist

### Book

Every book has its own screen displaying detailed information (main information, quotes, and photos) and 2 buttons:
- Add quote
- Add photo

Long-pressing a quote allows the user to edit or delete it.

Users can tap a photo to view it in full-screen mode. Long-pressing a photo provides the option to delete it.

### Find Quote

This screen contains a search bar where users can input a quote. The results are displayed below. If no quotes are found, a corresponding message is shown.

### Find Book or Author

Similar to the Find Quote screen, but for searching books and authors. Users can search within books, authors, or both.

### News

There are 7 types of news:

1. Book added
2. Book edited
3. Book read
4. Book deleted
5. Quote added
6. Photo added
7. Books imported

All news have a reading indicator. If user taps on the news, it is marked as read.

![first_design](first_design.jpg)

## For the Future

1. Add user account -> Data Base or cloud storage.
2. Subscriptions -> Notifications.
3. List of authors -> Author Page.
4. Archive of books.
5. Share quotes of a book.
6. CSV, XML, YAML import and export.
7. Saving photos.

## Updates

### Version 1.1 (27.03.25)

1. Fixed books edition.
2. Fixed search screen.
3. Fixed input fields.
4. Changed news from list to stack
5. Adding a reading indicator.

### Version 1.0 (24.03.25)

The main functions was created:

1. Adding of books, quotes and photos.
2. Editing books, quotes.
3. Deleting books, quotes, photos.
4. Moving books between "Read" and "Wishlist".
5. Search for books, authors and quotes.
6. Import and export of books.
7. News.