package com.findit.app

import com.findit.app.data.repository.AuthEmailPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthEmailPolicyTest {

    @Test
    fun allowsSchoolDomainCaseInsensitively() {
        assertTrue(AuthEmailPolicy.isAllowed("student@sjp2cd.edu.ph"))
        assertTrue(AuthEmailPolicy.isAllowed("Student@SJP2CD.EDU.PH"))
    }

    @Test
    fun allowsAdminExceptionCaseInsensitively() {
        assertTrue(AuthEmailPolicy.isAllowed("admin@school.edu"))
        assertTrue(AuthEmailPolicy.isAllowed("ADMIN@SCHOOL.EDU"))
    }

    @Test
    fun rejectsOtherDomainsAndDomainLookalikes() {
        assertFalse(AuthEmailPolicy.isAllowed("student@example.com"))
        assertFalse(AuthEmailPolicy.isAllowed("student@sjp2cd.edu.ph.example.com"))
        assertFalse(AuthEmailPolicy.isAllowed("admin@school.edu.example.com"))
    }
}
