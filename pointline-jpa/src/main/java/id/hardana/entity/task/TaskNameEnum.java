package id.hardana.entity.task;

/**
 * Created by pancara on 24/02/15.
 */
public enum TaskNameEnum {
    DISBURSEMENT(0, "DISBURSEMENT"),
    IMBURSEMENT(1, "IMBURSEMENT"),
    REPORT_DAILY(2, "REPORT_DAILY");

    private int id;
    private String name;

    private TaskNameEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static TaskNameEnum valueById(Integer id) {
        if (id == null)
            return null;

        switch (id) {
            case 0:
                return DISBURSEMENT;
            case 1:
                return IMBURSEMENT;
            case 2:
                return REPORT_DAILY;
            default:
                throw new EnumConstantNotPresentException(TaskNameEnum.class, id.toString());
        }
    }
}
