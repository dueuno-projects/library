package example

import dueuno.commons.utils.LogUtils
import dueuno.elements.ElementsController
import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentTable
import dueuno.elements.controls.*
import dueuno.elements.style.TextDefault
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct

@Slf4j
@Secured(['ROLE_USER', /* other ROLE_... */])
class BookController implements ElementsController {

    BookService bookService

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
            actionbar.with {
                addAction(controller: 'tag', icon: 'fa-tags')
            }
            sortable = [
                    title: 'asc',
            ]
            columns = [
                    'title',
                    'isbn',
                    'genre',
                    'publishedDate',
                    'price',
                    'inStock',
            ]

            body.eachRow { TableRow row, Map values ->
                // Do not execute slow operations here to avoid slowing down the table rendering
                row.cells.isbn.url = "https://www.amazon.com/s?k=${values.isbn}"
                row.cells.isbn.icon = 'fa-arrow-up-right-from-square'
                row.cells.isbn.tooltip = 'book.view.on.amazon'
                row.cells.price.tag = true
            }
        }

        c.table.body = bookService.list(c.table.filterParams, c.table.fetchParams)
        c.table.paginate = bookService.count(c.table.filterParams)

        display content: c
    }

    private buildForm(TBook obj = null, Boolean readonly = false) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        if (readonly) {
            c.header.removeNextButton()
            c.form.readonly = true
        }

        c.form.with {
            validate = TBook
            addField(
                    class: TextField,
                    id: 'title',
            )
            addField(
                    class: TextField,
                    id: 'isbn',
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'genre',
                    optionsFromList: TBook.constrainedProperties.genre.inList,
                    renderTextPrefix: false,
                    cols: 6,
            )
            addField(
                    class: Textarea,
                    id: 'description',
            )
            addField(
                    class: DateField,
                    id: 'publishedDate',
                    cols: 4,
            )
            addField(
                    class: MoneyField,
                    id: 'price',
                    currency: 'USD',
                    cols: 4,
            )
            addField(
                    class: Checkbox,
                    id: 'inStock',
                    cols: 4,
            )
            addField(
                    class: Select,
                    id: 'tags',
                    optionsFromRecordset: TTag.list(),
                    multiple: true,
                    help: 'book.tags.help',
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
        def obj = bookService.create(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def edit() {
        def obj = bookService.get(params.id)
        def c = buildForm(obj)
        display content: c, modal: true
    }

    def onEdit() {
        def obj = bookService.update(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def onDelete() {
        try {
            bookService.delete(params.id)
            display action: 'index'

        } catch (e) {
            display exception: e
        }
    }
}
