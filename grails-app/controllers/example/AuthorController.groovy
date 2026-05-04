package example

import dueuno.commons.utils.LogUtils
import dueuno.elements.ElementsController
import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentTable
import dueuno.elements.controls.Checkbox
import dueuno.elements.controls.EmailField
import dueuno.elements.controls.Select
import dueuno.elements.controls.TextField
import dueuno.elements.controls.Textarea
import dueuno.elements.controls.UrlField
import dueuno.elements.style.TextDefault
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct

@Slf4j
@Secured(['ROLE_USER', /* other ROLE_... */])
class AuthorController implements ElementsController {

    AuthorService authorService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    def handleException(Exception e) {
        // Display a popup message instead of the "Error" screen
        log.error LogUtils.logStackTrace(e)
        display exception: e
    }

    def index() {
        def c = createContent(ContentTable)
        c.table.with {
            filters.with {
                addField(
                        class: TextField,
                        id: 'find',
                        label: TextDefault.FIND,
                )
            }
            sortable = [
                    name: 'asc',
            ]
            columns = [
                    'name',
                    'email',
                    'website',
                    'contactInfo.phone',
                    'contactInfo.mailingAddress',
            ]

            body.eachRow { TableRow row, Map values ->
                // Do not execute slow operations here to avoid slowing down the table rendering
                if (values.website) {
                    row.cells.website.url = values.website
                    row.cells.website.icon = 'fa-arrow-up-right-from-square'
                }

                if (values.contactInfo.phone) {
                    row.cells.'contactInfo.phone'.icon = 'fa-phone'
                }

                if (values.contactInfo.mailingAddress) {
                    row.cells.'contactInfo.mailingAddress'.url = "https://www.google.com/maps/dir/?api=1&destination=${values.contactInfo.mailingAddress}"
                    row.cells.'contactInfo.mailingAddress'.icon = 'fa-location-arrow'
                    row.cells.'contactInfo.mailingAddress'.tooltip = 'author.view.on.google.maps'
                }
            }
        }

        c.table.body = authorService.list(c.table.filterParams, c.table.fetchParams)
        c.table.paginate = authorService.count(c.table.filterParams)

        display content: c
    }

    private buildForm(TAuthor obj = null, Boolean readonly = false) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        if (readonly) {
            c.header.removeNextButton()
            c.form.readonly = true
        }

        c.form.with {
            validate = TAuthor
            addField(
                    class: TextField,
                    id: 'name',
            )
            addField(
                    class: EmailField,
                    id: 'email',
                    cols: 6,
            )
            addField(
                    class: UrlField,
                    id: 'website',
                    cols: 6,
            )
            addField(
                    class: Textarea,
                    id: 'bio',
            )
            addField(
                    class: TextField,
                    id: 'contactInfo.phone',
                    cols: 6,
            )
            addField(
                    class: TextField,
                    id: 'contactInfo.mailingAddress',
                    cols: 6,
            )
        }

        if (obj) {
            c.form.values = obj
        }

        return c
    }

    def create() {
        def c = buildForm()
        display content: c, modal: true
    }

    def onCreate() {
        def obj = authorService.create(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def edit() {
        def obj = authorService.get(params.id)
        def c = buildForm(obj)
        display content: c, modal: true
    }

    def onEdit() {
        def obj = authorService.update(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def onDelete() {
        try {
            authorService.delete(params.id)
            display action: 'index'

        } catch (e) {
            display exception: e
        }
    }
}
