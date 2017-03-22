# JWL
Just Walk Out Library

## Task

**HaVH**
* [ ] Complete report 3.
* [x] View detail book with full information: use api: /books/{id}
* [ ] Add renew button at: get book list and get book detail
    + [x] Renew a book in view book detail
    + [ ] Renew a list of book in view list borrowing books
* [x] Notification when deadline remaining [3] days and on the deadline day
* [ ] Check borrower while checking in:
    + [ ] borrower is activated/unactivated?
    + [ ] borrower is in library?
    + [ ] borrower misses deadline of one of his books
    + [ ] borrower information appears in web for librarian to validate
* [ ] Init borrow:
    + [ ] borrower inits while another hasn't finished checking out
* [ ] Check borrow book:
    + [ ] Check the number of books a borrower can borrow
    + [ ] Check if a book can be borrowed or not
* [ ] Checkout:
    + [ ] timeout for checkout -> noti borrower that checkout failed
* [ ] Check after a book is returned:
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


**ThienDN** 
* [ ] Update report 3
* [x] Search books
	[x] Search api
	[x] Check available of book
	[x] Mobile implement
* [ ] Add to wish list
	+ [x] return 'available' state of book. numberOfCopies at BookEntity is number of 'total book copies'
	+ [x] api add to wishlist
	+ [x] api remove from wishlist
	+ [] notification method util (book's id is topic)
	+ [] implement mobile


## Optional
* [ ] Edit account
* [ ] Edit book
* [ ] Delete book
* [ ] Delete account (soft delete): check user are borrowing books or not. add delete_date and is_activated = false when not. In the other hand, just only change is_activated = false
* [ ] Print report for borrow and return books: restructure database