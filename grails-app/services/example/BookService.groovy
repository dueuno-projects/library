package example

import dueuno.audit.AuditOperation
import dueuno.audit.AuditService
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.transactions.Transactional
import groovy.contracts.Requires
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct

import java.time.LocalDate

@Slf4j
@CurrentTenant
@CompileStatic
class BookService {

    AuditService auditService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    @CompileDynamic
    private DetachedCriteria<TBook> buildQuery(Map filterParams) {
        def query = TBook.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }
        if (filterParams.containsKey('title')) query = query.where { title == filterParams.title }
        if (filterParams.containsKey('isbn')) query = query.where { isbn == filterParams.isbn }
        if (filterParams.containsKey('genre')) query = query.where { genre == filterParams.genre }
        if (filterParams.containsKey('publishedDate')) query = query.where { publishedDate == filterParams.publishedDate }
        if (filterParams.containsKey('inStock')) query = query.where { inStock == filterParams.inStock }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where {
                true
                        || title =~ "%${search}%"
                        || isbn =~ "%${search}%"
                        || genre =~ "%${search}%"
                        || description =~ "%${search}%"
            }
        }

        // Add additional filters here

        return query
    }

    private Map getFetchAll() {
        // Add any relationship here (Eg. references to other DomainObjects or hasMany)
        return [
                'author': 'join',

                // hasMany relationships
                'tags': 'join',
        ]
    }

    private Map getFetch() {
        // Add only single-sided relationships here (Eg. references to other Domain Objects)
        // DO NOT add hasMany relationships, you are going to have troubles with pagination
        return [
                'author': 'join',
        ]
    }

    @Requires({ id })
    TBook get(Serializable id) {
        return find(id: id)
    }

    TBook find(Map filterParams) {
        return buildQuery(filterParams).get(fetch: fetchAll)
    }

    List<TBook> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [name: 'asc']
        if (!fetchParams.fetch) fetchParams.fetch = fetch

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Number count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    @Transactional
    TBook create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TBook obj = new TBook(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    @CompileDynamic
    @Requires({ args.id })
    TBook update(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TBook obj = get(args.id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    @Requires({ id })
    void delete(Serializable id) {
        TBook obj = get(id)
        obj.delete(flush: true)
        auditService.log(AuditOperation.DELETE, obj)
    }
}
