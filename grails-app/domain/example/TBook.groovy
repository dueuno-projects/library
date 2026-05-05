package example

import dueuno.types.Money
import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDate

class TBook implements GormEntity, MultiTenant<TBook> {

    String title
    String isbn
    String genre
    String description
    LocalDate publishedDate
    Money price
    Boolean inStock = true

    static embedded = ['price']
    static belongsTo = [author: TAuthor]
    static hasMany = [tags: TTag]

    static constraints = {
        title blank: false, maxSize: 255
        isbn blank: false, matches: /^(?:\d{10}|\d{13}|\d{3}-\d-\d{2}-\d{6}-\d)$/
        genre inList: ['Fiction', 'Non-Fiction', 'Biography', 'Science', 'History', 'Poetry']
        description blank: false, maxSize: 2000, widget: 'textarea'
        publishedDate nullable: false
        price nullable: false
        inStock nullable: false
    }

}