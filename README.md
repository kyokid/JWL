# JWL
Just Walk Out Library

## Task

**HaVH**
* [ ] Complete report 3.
* [?] View detail book with full information: We have api to get book details AND its borrowing copies
* [?] View detail book can renew: What's the different from other kinds of books?
* [?] View list borrowing books can renew. Why? And what about books that cannot be renewed?
* ?[x]? Notification when deadline remaining [3] days EVERYDAY? And when will it end?
* [ ] Delete account (soft delete)
* [ ] Edit account
* [ ] Edit book
* [ ] Delete book
* [ ] Check borrower while checking in:
    + [ ] borrower is activated/unactivated?
    + [ ] borrower is in library?
    + [ ] borrower misses deadline of one of his books
    + [ ] borrower information appears in web for librarian to validate
* [ ] Init borrow:
    + [ ] borrower inits while another hasn't finished checking out
* [ ] Check borrow book:
    + [ ] Recalculate the number of available copies
    + [ ] Check the number of books a borrower can borrow
    + [ ] Check if a book can be borrowed or not
* [ ] Checkout:
    + [ ] timeout for checkout -> noti borrower that checkout failed
* [ ] Check after a book is returned:
    + [ ] Recalculate the number of available copies
    + [ ] Notify wish list
* [ ] Vietnamese/English on mobile
* [ ] Record sound

**Tuan Anh**
* [ ] Validate Add new account (???)
* [ ] Check role borrower when borrow book
* [ ] Authenticate account
* [ ] Get book detail
    + [x] api get book detail
    + [x] api get borrowing copies to view in book detail
* [ ] Get books
    + [x] api get all books
* [ ] Delete book
* [ ] Confirm dialog in delete command
* [x] Simulate return book with RFID
    + [x] api librarian add return copy to return cart
      + [x] check input rfid, return fail if the rfid is not being borrowed (or not exist at all)
      + [x] check to add new/update return cart of a librarian
      + [x] return confirmation code for web if librarian scan another user's book
    + [x] api cancel return copies
    + [x] api return copies


**HôiHángMộtTí** NO UPDATE?
* [ ] Update report 3
* [ ] Search books
* [ ] Add to wish list
