package dam.projects.projectdam.objects;

/**
 * Created by Renato on 01/06/2016 : 01:01.
 */
public class MemberInvite {
    // student who asks for the invite
    private int mi_id;
    private Member inviteMember;
    private EventFinal event;

    public MemberInvite(int mi_id, Member inviteMember, EventFinal event) {
        this.mi_id = mi_id;
        this.inviteMember = inviteMember;
        this.event = event;
    }

    public int getMi_id() {
        return mi_id;
    }

    public Member getInviteMember() {
        return inviteMember;
    }

    public EventFinal getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "MemberInvite{" +
                "mi_id=" + mi_id +
                ", inviteMember=" + inviteMember +
                ", event=" + event +
                '}';
    }
}
