package example

import dueuno.core.ApplicationService

class BootStrap {

    ApplicationService applicationService
    InstallService installService

    def init = {

        applicationService.onDevInstall { String tenantId ->
            installService.install()
        }

        applicationService.onInit {
            registerPrettyPrinter(TAuthor, '${it.name}')
            registerPrettyPrinter(TBook, '${it.title}')
            registerPrettyPrinter(TTag, '${it.name}')


            registerCredits('Original Apache Grails App', 'James Fredley')
            registerCredits('Dueuno Version', 'Gianluca Sartori')

            registerFeature(
                    controller: 'book',
                    icon: 'fa-book',
                    favourite: true,
            )
            registerFeature(
                    controller: 'author',
                    icon: 'fa-user',
                    favourite: true,
            )
        }

    }

    def destroy = {
    }

}