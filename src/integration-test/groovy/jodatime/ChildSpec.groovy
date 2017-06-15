package jodatime

import grails.test.mixin.integration.Integration
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import spock.lang.Specification
import spock.lang.Unroll

import java.sql.Date

@Integration
class ChildSpec extends Specification {

    SessionFactory sessionFactory

    def setup() {
    }

    def cleanup() {
    }

    @Unroll("Test type of #field from #type")
    void "Test type of dates "() {
        given:
        Child.withTransaction {
            def domain = type.newInstance()
            data.each { key, value ->
                domain[key] = value
            }
            domain.save(flush: true)
        }

        when:
        def resultSet
        Child.withTransaction {
            def connection = sessionFactory.getCurrentSession().connection()
            def stmt = connection.prepareStatement("select ${field} from ${table}")
            resultSet = stmt.executeQuery()
        }

        then:
        resultSet.next()
        and:
        resultSet.getObject(1).class == expectedType

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
