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

@Slf4j
@CurrentTenant
@CompileStatic
class AuthorService {

    AuditService auditService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    @CompileDynamic
    private DetachedCriteria<TAuthor> buildQuery(Map filterParams) {
        def query = TAuthor.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where {
                true
                        || name =~ "%${search}%"
                        || email =~ "%${search}%"
                        || website =~ "%${search}%"
                        || bio =~ "%${search}%"
            }
        }

        // Add additional filters here

        return query
    }

    private Map getFetchAll() {
        // Add any relationship here (Eg. references to other DomainObjects or hasMany)
        return [
                'contactInfo': 'join',

                // hasMany relationships
                'books'      : 'join',
        ]
    }

    private Map getFetch() {
        // Add only single-sided relationships here (Eg. references to other Domain Objects)
        // DO NOT add hasMany relationships, you are going to have troubles with pagination
        return [
                'contactInfo': 'join',
        ]
    }

    @Requires({ id })
    TAuthor get(Serializable id) {
        return find(id: id)
    }

    TAuthor find(Map filterParams) {
        return buildQuery(filterParams).get(fetch: fetchAll)
    }

    List<TAuthor> list(Map filterParams = [:], Map fetchParams = [:]) {
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
    TAuthor create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TAuthor obj = new TAuthor(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    @CompileDynamic
    @Requires({ args.id })
    TAuthor update(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TAuthor obj = get(args.id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    @Requires({ id })
    void delete(Serializable id) {
        TAuthor obj = get(id)
        obj.delete(flush: true)
        auditService.log(AuditOperation.DELETE, obj)
    }
}
