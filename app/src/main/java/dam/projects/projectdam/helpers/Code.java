package dam.projects.projectdam.helpers;
@SuppressWarnings("unused")
/**
 * Created by Renato on 28/04/2016 : 20:22.
 * Enumerator that stores general codes, integer type, to be used everywhere.
 */
public enum Code {
    // region NETWORK CODES
    NET_SUCCESS_COD(100),
    NET_GENERIC_ERR(101),
    NET_PROXY_ERR(102),
    NET_SERVER_ERR(103),
    // endregion

    // region CODE ERRORS
    COD_UNHANDLED_EXC(50),
    // endregion

    // region FRIEND CODES
    INVALID_STUDENT(30),
    ADD_FRIEND_SUC(31),
    ADD_FRIEND_ERR(32),
    ADD_ALREADY_FRI(12),
    ADD_ALREADY_INV(12),
    ADD_SAME_USER(13),

    ACP_FRIEND_SUC(33),
    ACP_FRIEND_ERR(34),

    GET_FRIENDS_SUC(35),
    GET_FRIENDS_ERR(36),

    GET_FRI_INV_SUC(35),
    GET_FRI_INV_ERR(36),

    RMV_FRIEND_SUC(37),
    RMV_FRIEND_ERR(38),

    DCL_FRIEND_SUC(39),
    DCL_FRIEND_ERR(40),
    // endregion

    // region ACTIVITY RESULT CODES
    // endregion

    CHK_STUDENT_SUC(10),
    CHK_STUDENT_ERR(11),

    //region EVENTS CODES
    GET_EVENTS_PUB_SUC(44),
    GET_EVENTS_PUB_ERR(45),
    GET_EVENTS_FRI_SUC(45),
    GET_EVENTS_FRI_ERR(46),
    GET_EVENTS_PRI_SUC(47),
    GET_EVENTS_PRI_ERR(48),

    ADD_EVENT_SUC(51),
    RMV_EVENT_SUC(52),
    ADD_EVENT_TYPE_VISIB_ERR(53),
    ADD_EVENT_DATE_INV_ERR(54),
    ADD_EVENT_ERR(55),

    //endregion

    //region SCHEDULER CODES
    GET_SCHEDULE_SUC(200),
    GET_SCHEDULE_ERR(201),
    GET_SCHEDULE_NULL(202),
    //endregion

    //region GRADE CODES
    GET_GRADE_SUC(300),
    GET_GRADE_ERR(301),
    GET_GRADE_NULL(302),
    //endregion

    //region NOTIFICATION_CODES
    FRIENDS_NOTIFICATION(401),
    GRADES_NOTIFICATION(402),
    //endregion

    //region MEMBERS CODE
    GET_MEMBERS_SUC(600),
    GET_MEMBERS_ERR(601),

    GET_MEM_INV_SUC(602),
    GET_MEM_INV_ERR(603),

    ACP_MEMBER_SUC(604),
    ACP_MEMBER_ERR(605),

    RMV_MEMBER_SUC(606),
    RMV_MEMBER_ERR(607),

    ADD_MEMBER_SAME_USER(608),              //3
    ADD_MEMBER_NO_EVENT(609),               //4
    ADD_MEMBER_ALREADY_MEMBER(610),         //6
    ADD_MEMBER_NOT_ALLOW(611),              //5
    ADD_MEMBER_NOT_FRIEND(611),             //7

    ADD_MEMBER_ERR(612),
    ADD_MEMBER_SUC(613),

    RATE_EVENT_SUC(650),
    RATE_EVENT_ERR(651),
    UNRATE_EVENT_SUC(652),
    UNRATE_EVENT_ERR(653),

    FAV_EVENT_SUC(655),
    FAV_EVENT_ERR(656),
    UNFAV_EVENT_SUC(657),
    UNFAV_EVENT_ERR(658),

    //endregion
    ;
    public final int code;

    Code(int code) {
        this.code = code;
    }
}
