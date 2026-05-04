package example

class TContactInfo {

    String phone
    String mailingAddress

    static belongsTo = [author: TAuthor]

    static constraints = {
        phone blank: false, maxSize: 32
        mailingAddress blank: false, maxSize: 500, widget: 'textarea'
    }

}