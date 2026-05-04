package example

class TTag {

    String name

    static hasMany = [books: TBook]
    static belongsTo = TBook

    static constraints = {
        name blank: false, maxSize: 60, unique: true
    }

}