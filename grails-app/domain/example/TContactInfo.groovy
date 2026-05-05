package example

import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

class TContactInfo implements GormEntity, MultiTenant<TContactInfo> {

    String phone
    String mailingAddress

    static belongsTo = [author: TAuthor]

    static constraints = {
        phone blank: false, maxSize: 32
        mailingAddress blank: false, maxSize: 500, widget: 'textarea'
    }

}