package org.example.project.network

object ApiEndpoints {
    const val LOGIN = "user/login"
    const val FORGOT_PASSWORD = "forgot-password"
    const val REGISTER = "user/registration"
    const val VERIFY_OTP = "user/email-verify"
    const val USER_CHECKOUT = "generic/user-check"
    const val PROJECT_LIST = "v3/group/list"
    const val REFRESH_TOKEN = "token/refresh"
    const val HOME_CONTENT = "home/contents"

    const val NOTIFICATION_LIST = "v3/notification/list"
    const val USER_EDIT = "user/profile/edit"
    const val CREATE_PROJECT = "v3/group/create"
    const val VIEW_PROJECT = "v3/group/view"
    const val REQUEST_PROJECT_ACCESS = "v3/group/request-access"
    const val UPLOAD_IMAGE = "upload-image"
    const val PROJECT_DETAILS = "v3/group/details"
    const val INVITE_MEMBERS = "group/invite-users"
    const val DELETE_PROJECT = "group/delete"
    const val EXIT_PROJECT = "group/exit"
    const val CHANGE_ROLE = "group/role-change"
    const val REMOVE_MEMBER = "group/remove-member"
}
