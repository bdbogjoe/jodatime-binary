package jodatime

import grails.test.spock.IntegrationSpec
import org.hibernate.SessionFactory
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import spock.lang.Unroll

import java.sql.Date

class ChildSpec extends IntegrationSpec {

    SessionFactory sessionFactory

    def setup() {
    }

    def cleanup() {
    }

    @Unroll("Test type of #field from #type")
    void "Test type of dates "() {
        given:
        def domain = type.newInstance()
        data.each { key, value ->
            domain[key] = value
        }
        domain.save(flush: true)

        when:
        def connection = sessionFactory.getCurrentSession().connection()
        def stmt = connection.prepareStatement("select ${field} from ${table}")
        def resultSet = stmt.executeQuery()

        then:
        resultSet.next()
        and:
        resultSet.getObject(1).class == expectedType

        cleanup:
        resultSet.close()
        stmt.close()

        where:
        type  | expectedType | field           | table   | data
        Main  | Date         | 'my_date'       | 'main'  | [myDate    : LocalDate.now(),
                                                            myDateTime: LocalDateTime.now()]
        Child | Date         | 'my_child_date' | 'child' | [myChildDate    : LocalDate.now(),
                                                            myChildDateTime: LocalDateTime.now(),
                                                            myDate         : LocalDate.now(),
                                                            myDateTime     : LocalDateTime.now()]


    }
}
