package example

import dueuno.types.Money
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

import java.time.LocalDate

@Slf4j
@CurrentTenant
class InstallService {

    void install() {
        TTag fantasy = new TTag(name: 'Fantasy').save(failOnError: true)
        TTag classic = new TTag(name: 'Classic').save(failOnError: true)
        TTag romance = new TTag(name: 'Romance').save(failOnError: true)
        TTag adventure = new TTag(name: 'Adventure').save(failOnError: true)

        TAuthor tolkien = new TAuthor(
                name: 'J.R.R. Tolkien',
                email: 'jrr@example.com',
                bio: 'English writer and philologist, best known for The Hobbit and The Lord of the Rings.',
                website: 'https://www.tolkienestate.com/'
        )
        tolkien.contactInfo = new TContactInfo(
                phone: '+44 20 7946 0958',
                mailingAddress: '1 Oxford Way, Oxford, England'
        )
        tolkien.save(failOnError: true)

        TAuthor austen = new TAuthor(
                name: 'Jane Austen',
                email: 'jane@example.com',
                bio: 'English novelist known primarily for her six major novels of the early 19th century.',
                website: null
        )
        austen.contactInfo = new TContactInfo(
                phone: '+44 1256 462100',
                mailingAddress: 'Steventon Rectory, Hampshire, England'
        )
        austen.save(failOnError: true)

        TBook hobbit = new TBook(
                title: 'The Hobbit',
                isbn: '9780547928227',
                genre: 'Fiction',
                description: 'A reluctant hobbit, Bilbo Baggins, sets out to the Lonely Mountain with a spirited group of dwarves to reclaim their mountain home.',
                publishedDate: LocalDate.of(1937, 9, 21),
                price: new Money(14.99, 'USD'),
                inStock: true,
                author: tolkien
        )
        hobbit.addToTags(fantasy).addToTags(adventure).addToTags(classic).save(failOnError: true)

        TBook pride = new TBook(
                title: 'Pride and Prejudice',
                isbn: '9780141439518',
                genre: 'Fiction',
                description: 'The story of Mr Bennet of Longbourn estate and his five daughters on the lookout for marriage.',
                publishedDate: LocalDate.of(1813, 1, 28),
                price: new Money(9.99, 'USD'),
                inStock: true,
                author: austen
        )
        pride.addToTags(romance).addToTags(classic).save(failOnError: true)
    }

}
