package dam.projects.projectdam.json.server.event.member.invite;

/**
 * Created by Renato on 30/05/2016 : 00:43.
 */
public class JMemberInv {
    public Integer event_id, e_type_id, e_visibi_id,
            e_number_members, m_id, m_student_id, m_favorite, m_rating;
    public String e_admin, e_type_code, e_visibi_code, e_title,
            e_desc, e_date_begin, e_hour_begin, e_date_end, e_hour_end,
            e_date_create, e_local, m_student_number, m_student_name,
            m_student_mail, m_student_photo;
    public Float e_average_rating;
}