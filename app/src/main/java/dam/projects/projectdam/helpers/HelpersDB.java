package dam.projects.projectdam.helpers;

import java.util.ArrayList;
import java.util.List;

import dam.projects.projectdam.json.server.event.JEvent;
import dam.projects.projectdam.json.server.event.JResultEvent;
import dam.projects.projectdam.json.server.event.member.JMember;
import dam.projects.projectdam.json.server.event.member.JResultMember;
import dam.projects.projectdam.json.server.event.member.invite.JMemberInv;
import dam.projects.projectdam.json.server.event.member.invite.JResultMemberInv;
import dam.projects.projectdam.json.server.friend.JFriend;
import dam.projects.projectdam.json.server.friend.JResultFriend;
import dam.projects.projectdam.json.siupt.grades.JGradeResultUPT;
import dam.projects.projectdam.json.siupt.grades.JGradeUPT;
import dam.projects.projectdam.json.siupt.schedule.JScheduleResultUPT;
import dam.projects.projectdam.json.siupt.schedule.JScheduleUPT;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.objects.AcademicYear;
import dam.projects.projectdam.objects.EventFinal;
import dam.projects.projectdam.objects.EventType;
import dam.projects.projectdam.objects.Friend;
import dam.projects.projectdam.objects.Grade;
import dam.projects.projectdam.objects.Member;
import dam.projects.projectdam.objects.MemberInvite;
import dam.projects.projectdam.objects.ScheduleDay;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.objects.VisibilityType;

/**
 * Created by Renato on 04/06/2016 : 23:31.
 */
public class HelpersDB {

    public static Grade[] convertGrades(JGradeResultUPT json, HttpNRequest request) {
        ArrayList<Grade> list = new ArrayList<>();
        for (JGradeUPT each : json.result) {
            list.add(new Grade(each.pv_data_inicio == null ? null : HelpersDate.stringToDateTimeUPT(each.pv_data_inicio),
                    each.d_nome,
                    each.pvep_nome,
                    each.avi_nome,
                    each.dfm_nome,
                    each.pvn_media,
                    each.pvn_observacao,
                    each.pv_observacao,
                    each.observacao,
                    each.nota,
                    each.est_nome,
                    each.assiduidade,
                    each.est_assiduidade,
                    each.dfm_nota_minima,
                    each.dfm_peso,
                    (request.getParametersDetails()[5].equals("null") ? null : Integer.parseInt(request.getParametersDetails()[5])),
                    (request.getParametersDetails()[6].equals("null") ? null : Integer.parseInt(request.getParametersDetails()[6])),
                    (request.getParametersDetails()[4].equals("null") ? null : Integer.parseInt(request.getParametersDetails()[4])),
                    AcademicYear.toObject(request.getParametersDetails()[3])));
        }
        return list.toArray(new Grade[list.size()]);
    }

    public static MemberInvite[] convertMembersInv(JResultMemberInv json) {
        ArrayList<MemberInvite> list = new ArrayList<>();
        for (JMemberInv each : json.result) {
            list.add(new MemberInvite(
                    each.m_id,
                    new Member(
                            each.m_id,
                            each.event_id,
                            each.m_rating,
                            new Student(
                                    0,
                                    each.m_student_number,
                                    each.m_student_name,
                                    each.m_student_mail,
                                    each.m_student_photo
                            ),
                            each.m_favorite == 1),
                    new EventFinal(
                            each.event_id,
                            Integer.parseInt(each.e_admin),
                            new EventType(each.e_type_id, each.e_type_code),
                            new VisibilityType(each.e_visibi_id, each.e_visibi_code),
                            each.e_title,
                            each.e_desc,
                            each.e_local,
                            HelpersDate.stringToDate(each.e_date_begin),
                            each.e_date_end != null ? HelpersDate.stringToDate(each.e_date_end) : null,
                            each.e_hour_begin != null ? HelpersDate.stringToHour(each.e_hour_begin) : null,
                            each.e_hour_end != null ? HelpersDate.stringToHour(each.e_hour_end) : null,
                            HelpersDate.stringToDateTimeUPT(each.e_date_create),
                            each.e_number_members,
                            each.e_average_rating
                    )));
        }
        return list.toArray(new MemberInvite[list.size()]);
    }

    public static Member[] convertMembers(JResultMember json) {
        ArrayList<Member> list = new ArrayList<>();
        for (JMember each : json.result) {
            list.add(new Member(
                    each.m_id,
                    each.e_id,
                    each.m_rating,
                    new Student(
                            0,
                            each.m_student_number,
                            each.m_student_name,
                            each.m_student_mail,
                            each.m_student_photo
                    ),
                    each.m_favorite == 1
            ));
        }
        return list.toArray(new Member[list.size()]);
    }

    public static EventFinal[] convertEvents(JResultEvent json) {
        ArrayList<EventFinal> list = new ArrayList<>();
        for (JEvent each : json.result) {
            list.add(new EventFinal(
                    each.e_id,
                    Integer.parseInt(each.e_admin),
                    new EventType(each.e_type_id, each.e_type_code),
                    new VisibilityType(each.e_visibi_id, each.e_visibi_code),
                    each.e_title,
                    each.e_desc,
                    each.e_local,
                    HelpersDate.stringToDate(each.e_date_begin),
                    each.e_date_end != null ? HelpersDate.stringToDate(each.e_date_end) : null,
                    each.e_hour_begin != null ? HelpersDate.stringToHour(each.e_hour_begin) : null,
                    each.e_hour_end != null ? HelpersDate.stringToHour(each.e_hour_end) : null,
                    HelpersDate.stringToDateTimeUPT(each.e_date_create),
                    each.e_number_members,
                    each.e_average_rating

            ));
        }
        return list.toArray(new EventFinal[list.size()]);
    }

    public static Friend[] jsonIntoFriends(JResultFriend json, boolean isInvite) {
        List<JFriend> jFriends = json.result;
        ArrayList<Friend> friends = new ArrayList<>();
        for (JFriend each : jFriends) {
            friends.add(new Friend(each.friend_name, each.friend_id, each.friend_mail,
                    each.friend_photo, null, isInvite));
        }
        return friends.toArray(new Friend[friends.size()]);
    }

    public static ScheduleDay[] convertSchedule(JScheduleResultUPT json, HttpNRequest request) {
        ArrayList<ScheduleDay> list = new ArrayList<>();
        for (JScheduleUPT each : json.result) {
            list.add(new ScheduleDay(each.horario_id, each.disciplina_nome,
                    each.tipo_ensino_nome, each.tipo_ensino_abreviatura,
                    each.sala_nome, each.turma_nome,
                    HelpersDate.dateStringToHour(each.horario_hora_inicio),
                    HelpersDate.dateStringToHour(each.horario_hora_fim),
                    each.horario_duracao, (each.horario_dia_semana),
                    AcademicYear.toObject(request.getParametersDetails()[3]), Integer.parseInt(request.getParametersDetails()[4])));
        }
        return list.toArray(new ScheduleDay[list.size()]);
    }

}
