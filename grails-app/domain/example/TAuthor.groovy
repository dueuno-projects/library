package example

import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

class TAuthor implements GormEntity, MultiTenant<TAuthor> {

    String name
    String email
    String bio
    String website

    static hasOne = [contactInfo: TContactInfo]
    static hasMany = [books: TBook]

    static constraints = {
        name blank: false, maxSize: 200
        email email: true, blank: false
        bio blank: false, maxSize: 4000, widget: 'textarea'
        website url: true, nullable: true
        contactInfo nullable: true
    }

}