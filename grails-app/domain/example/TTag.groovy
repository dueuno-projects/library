package example

import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

class TTag implements GormEntity, MultiTenant<TTag> {

    String name

    static hasMany = [books: TBook]
    static belongsTo = TBook

    static constraints = {
        name blank: false, maxSize: 60, unique: true
    }

}