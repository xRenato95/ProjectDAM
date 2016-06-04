package dam.projects.projectdam.objects;

/**
 * Created by Renato on 20/05/2016 : 17:24.
 * Class that represents an academic year. Ex: "2015/2016"
 */
public class AcademicYear {
    private int firstYear, secondYear;
    private static final String SEPARATOR = "/";

    public AcademicYear(int firstYear, int secondYear) {
        this.firstYear = firstYear;
        this.secondYear = secondYear;
    }

    public static AcademicYear toObject(String academicYear) {
        if (academicYear == null) return null;

        String[] years = academicYear.split(SEPARATOR);
        if (years.length != 2) return null;

        int year01, year02;
        try {
            year01 = Integer.parseInt(years[0]);
            year02 = Integer.parseInt(years[1]);
        } catch (Exception e) {
            return null;
        }
        return new AcademicYear(year01, year02);
    }

    public int getFirstYear() {
        return firstYear;
    }

    public int getSecondYear() {
        return secondYear;
    }

    @Override
    public String toString() {
        return getFirstYear()+SEPARATOR+getSecondYear();
    }
}
