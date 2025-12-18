package demo;

public class CardError {

    private static final int CARD_BASE = 0x1000;
    private static final int CARD_ERROR_SPAN = 0x50;
    private static final int ERROR_MSR_BASE = -CARD_BASE - 1; // msr card error base
    private static final int ERROR_ICC_CTLS_BASE = -((CARD_ERROR_SPAN << 1) + 1); // icc and ctls card error base
    private static final int ERROR_SAM_BASE = -(CARD_ERROR_SPAN * 3 + 1); // sam card error base

    /**
     * MSR card detected - 检测到磁条卡
     */
    public static final int MSR_DETECTED = CARD_BASE + 1;
    /**
     * ICC card detected - 检测到IC卡
     */
    public static final int ICC_DETECTED = MSR_DETECTED + 1;

    /**
     * CTLS card detected - 检测到非接卡
     */
    public static final int CTLS_DETECTED = ICC_DETECTED + 1;

    //
    // MSR card error
    //
    /**
     * MSR detected a swiping action, but no any data is generated. Please swipe again.
     * - 检测到磁条卡，但没有读取到数据，请重新刷卡
     */
    public static final int ERROR_MSR_SWIPE_AGAIN = ERROR_MSR_BASE;
    /**
     * MSR card data incorrect, track 2 data not found - 检测到磁条卡，但之二磁道数据缺失
     */
    public static final int ERROR_MSR_DATA_INCORRECT = -ERROR_MSR_SWIPE_AGAIN - 1;
    /**
     * MSR card key parameter incorrect - 检测到磁条卡，但密钥参数异常
     */
    public static final int ERROR_MSR_KEY_PARAM = -ERROR_MSR_DATA_INCORRECT - 1;


    //
    // ICC and CTLS card error
    //
    /**
     * ICC card inserted or contactless detected, but unknown card type - 检测到IC卡插入，但卡片异常，未知卡片类型
     */
    public static final int ERROR_CARD_UNKNOWN = ERROR_ICC_CTLS_BASE;
    /**
     * ICC card data incorrect - 检测到IC卡插入，但卡片数据异常
     */
    public static final int ERROR_ICC_DATA = ERROR_CARD_UNKNOWN - 1;
    /**
     * CTLS card inserted, but maybe it is M1 or M0 card - 检测到非接卡，但卡片M1或者M0卡
     */
    public static final int ERROR_CTLS_FOUND_MF = ERROR_ICC_DATA - 1;
    /**
     * 检测到非接卡，但卡片冲突
     */
    public static final int ERROR_CTLS_COLLISION = ERROR_CTLS_FOUND_MF - 1;
    /**
     * 检测到非接卡，但卡片数据异常
     */
    public static final int ERROR_CTLS_DATA = ERROR_CTLS_COLLISION - 1;

    //
    // PSAM card error
    public static final int ERROR_PSAM_EXCHANGE = ERROR_SAM_BASE - 1;
    // sim as PSAM
    public static final int ERROR_SIM_SAM = ERROR_PSAM_EXCHANGE - 1;
    public static final int ERROR_SIM_SAM_CON = ERROR_SIM_SAM - 1;
    public static final int ERROR_SIM_SAM_TRANSPORT = ERROR_SIM_SAM_CON - 1;


}
