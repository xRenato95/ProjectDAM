package dam.projects.projectdam.objects;

import org.joda.time.DateTime;

/**
 * Created by Renato on 29/05/2016 : 03:27.
 */
public class Grade {
    private int g_id;
    private DateTime dateBegin;
    private String courseName, pvepName, aviName,
                    evaluationName, averageGrade,
                    pvnObservation, pcObservation,
                    observation, grade, studentStatute,
                    assiduity;
    private int estAssiduidade, minimumGrade, gradeFinalWeight;

    private Integer era, state, semester;
    private AcademicYear academicYear;

    public Grade(DateTime dateBegin, String courseName, String pvepName, String aviName, String evaluationName, String averageGrade, String pvnObservation, String pcObservation, String observation, String grade, String studentStatute, String assiduity, int estAssiduidade, int minimumGrade, int gradeFinalWeight, Integer era, Integer state, Integer semester, AcademicYear academicYear) {
        this.dateBegin = dateBegin;
        this.courseName = courseName;
        this.pvepName = pvepName;
        this.aviName = aviName;
        this.evaluationName = evaluationName;
        this.averageGrade = averageGrade;
        this.pvnObservation = pvnObservation;
        this.pcObservation = pcObservation;
        this.observation = observation;
        this.grade = grade;
        this.studentStatute = studentStatute;
        this.assiduity = assiduity;
        this.estAssiduidade = estAssiduidade;
        this.minimumGrade = minimumGrade;
        this.gradeFinalWeight = gradeFinalWeight;
        this.era = era;
        this.state = state;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    public Grade(int g_id, DateTime dateBegin, String courseName, String pvepName, String aviName, String evaluationName, String averageGrade, String pvnObservation, String pcObservation, String observation, String grade, String studentStatute, String assiduity, int estAssiduidade, int minimumGrade, int gradeFinalWeight, Integer era, Integer state, Integer semester, AcademicYear academicYear) {
        this.g_id = g_id;
        this.dateBegin = dateBegin;
        this.courseName = courseName;
        this.pvepName = pvepName;
        this.aviName = aviName;
        this.evaluationName = evaluationName;
        this.averageGrade = averageGrade;
        this.pvnObservation = pvnObservation;
        this.pcObservation = pcObservation;
        this.observation = observation;
        this.grade = grade;
        this.studentStatute = studentStatute;
        this.assiduity = assiduity;
        this.estAssiduidade = estAssiduidade;
        this.minimumGrade = minimumGrade;
        this.gradeFinalWeight = gradeFinalWeight;
        this.era = era;
        this.state = state;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    public int getG_id() {
        return g_id;
    }

    public DateTime getDateBegin() {
        return dateBegin;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getPvepName() {
        return pvepName;
    }

    public String getAviName() {
        return aviName;
    }

    public String getEvaluationName() {
        return evaluationName;
    }

    public String getAverageGrade() {
        return averageGrade;
    }

    public String getPvnObservation() {
        return pvnObservation;
    }

    public String getPcObservation() {
        return pcObservation;
    }

    public String getObservation() {
        return observation;
    }

    public String getGrade() {
        return grade;
    }

    public String getStudentStatute() {
        return studentStatute;
    }

    public String getAssiduity() {
        return assiduity;
    }

    public int getEstAssiduidade() {
        return estAssiduidade;
    }

    public int getMinimumGrade() {
        return minimumGrade;
    }

    public int getGradeFinalWeight() {
        return gradeFinalWeight;
    }

    public Integer getEra() {
        return era;
    }

    public Integer getState() {
        return state;
    }

    public Integer getSemester() {
        return semester;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "g_id=" + g_id +
                ", dateBegin=" + dateBegin +
                ", courseName='" + courseName + '\'' +
                ", pvepName='" + pvepName + '\'' +
                ", aviName='" + aviName + '\'' +
                ", evaluationName='" + evaluationName + '\'' +
                ", averageGrade='" + averageGrade + '\'' +
                ", pvnObservation='" + pvnObservation + '\'' +
                ", pcObservation='" + pcObservation + '\'' +
                ", observation='" + observation + '\'' +
                ", grade='" + grade + '\'' +
                ", studentStatute='" + studentStatute + '\'' +
                ", assiduity='" + assiduity + '\'' +
                ", estAssiduidade=" + estAssiduidade +
                ", minimumGrade=" + minimumGrade +
                ", gradeFinalWeight=" + gradeFinalWeight +
                ", era=" + era +
                ", state=" + state +
                ", semester=" + semester +
                ", academicYear=" + academicYear +
                '}';
    }
/*
    {
      "pv_data_inicio": "2014-01-18 09:30",
      "d_id": "1679091c5a880faf6fb5e6087eb1b2dc",
      "d_nome": "Álgebra Linear",
      "pvep_nome": "Época normal",
      "avi_id": 1,
      "avi_nome": "Prova Escrita – Exame Final",
      "dfm_nome": "Teste 2",
      "pvn_media": "10,1",
      "pvn_observacao": null,
      "pv_observacao": "horário e local para consulta de prova: ",
      "observacao": "inscrito a 2013-11-29 às 10:27 ",
      "nota": "15,4",
      "est_id": null,
      "est_nome": "Aluno Ordinário",
      "estAssiduidade": 1,
      "dfm_peso": 45,
      "dfm_nota_minima": 0,
      "assiduidade": "0 %"
    }
    */
}
