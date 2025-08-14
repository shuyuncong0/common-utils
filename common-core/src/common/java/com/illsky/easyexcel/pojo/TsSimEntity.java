/**
* All Rights Reserved , Copyright (C) 2023 , 青岛鼎信通讯股份有限公司
*
* TsSimEntity
* SIM卡档案
*
* 修改纪录
* 2023-08-26 版本：1.0 wangzhengpu 创建。
* @version 版本：1.0
* @author 作者：wangzhengpu</name>
* 创建日期2023-08-26</date>
*/

package com.illsky.easyexcel.pojo;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;
import java.util.Date;

public class TsSimEntity
{
    private static final long serialVersionUID = 1L;
    /** TableName:SIM卡档案
	*/
    public static String tableName= "TsSim";
    public String getTableName(){ return "TsSim";}

    /** ColumnName:ICCID
	*/
    public static String FieldIccid = "iccid";

    /** ColumnName:MSISDN
	*/
    public static String FieldMsisdn = "msisdn";

    /** ColumnName:IMSI
	*/
    public static String FieldImsi = "imsi";

    /** ColumnName:平台类型
	*/
    public static String FieldPlatformtype = "platformtype";

    /** ColumnName:开户日期	费用/资产/代采购/外包
	*/
    public static String FieldOpeningdate = "openingdate";

    /** ColumnName:开户人
	*/
    public static String FieldHolder = "holder";

    /** ColumnName:激活日期
	*/
    public static String FieldActivationdate = "activationdate";

    /** ColumnName:卡状态
	*/
    public static String FieldState = "state";

    /** ColumnName:中移物联卡状态
	*/
    public static String FieldOnelinkstate = "onelinkstate";

    /** ColumnName:卡状态修改日期
	*/
    public static String FieldStatemodifiedon = "statemodifiedon";

    /** ColumnName:套餐年限
	*/
    public static String FieldPackagelife = "packagelife";

    /** ColumnName:套餐类型
	*/
    public static String FieldPackagetype = "packagetype";

    /** ColumnName:套餐到期时间
	*/
    public static String FieldPackageexpiration = "packageexpiration";

    /** ColumnName:套餐状态
	*/
    public static String FieldPackagestate = "packagestate";

    /** ColumnName:套餐状态修改日期
	*/
    public static String FieldPackagestatemodifiedon = "packagestatemodifiedon";

    /** ColumnName:本月套餐总量
	*/
    public static String FieldPackagecount = "packagecount";

    /** ColumnName:是否加入流量池
	*/
    public static String FieldIsflowpool = "isflowpool";

    /** ColumnName:导入时间
	*/
    public static String FieldImporttime = "importtime";

    /** ColumnName:是否同步流向
	*/
    public static String FieldIssync = "issync";

    /** ColumnName:流向
	*/
    public static String FieldBindingstate = "bindingstate";

    /** ColumnName:维护人
	*/
    public static String FieldMaintainer = "maintainer";

    /** ColumnName:登记日期
	*/
    public static String FieldRegistrationdate = "registrationdate";

    /** ColumnName:设备ID
	*/
    public static String FieldEquipmentid = "equipmentid";

    /** ColumnName:通讯地址
	*/
    public static String FieldMailingid = "mailingid";

    /** ColumnName:主站端口
	*/
    public static String FieldServerport = "serverport";

    /** ColumnName:产品名称
	*/
    public static String FieldProductname = "productname";

    /** ColumnName:产品类型	故指/低电压/负荷
	*/
    public static String FieldProducttype = "producttype";

    /** ColumnName:来源id	云主站ID
	*/
    public static String FieldSourceid = "sourceid";

    /** ColumnName:来源类型	故指/低电压/负荷
	*/
    public static String FieldSourcetype = "sourcetype";

    /** ColumnName:省区编码
	*/
    public static String FieldDeptcode = "deptcode";

    /** ColumnName:省区名称
	*/
    public static String FieldDeptname = "deptname";

    /** ColumnName:服务状态
	*/
    public static String FieldServicestate = "servicestate";

    /**ICCID
	*/
    @ExcelProperty("ICCID")
    private String iccid;

    /**MSISDN
	*/
    @ExcelProperty("MSISDN")
    private String msisdn;

    /**IMSI
	*/
    @ExcelProperty("IMSI")
    private String imsi;

    /**平台类型
	*/
    @ExcelProperty("平台类型")
    private String platformtype;

    /**开户日期	费用/资产/代采购/外包
	*/
    @ExcelProperty("开户日期")
    private Date openingdate;

    /**开户人
	*/
    @ExcelProperty("开户人")
    private String holder;

    /**激活日期
	*/
    @ExcelProperty("激活日期")
    private Date activationdate;

    /**卡状态
	*/
    private String state;

    /**中移物联卡状态
	*/
    @ExcelProperty("卡状态")
    private String onelinkstate;

    /**卡状态修改日期
	*/
    private Date statemodifiedon;

    /**套餐年限
	*/
    @ExcelProperty("套餐年限")
    private String packagelife;

    /**套餐类型
	*/
    @ExcelProperty("套餐类型")
    private String packagetype;

    /**套餐到期时间
	*/
    @ExcelProperty("套餐到期时间")
    private Date packageexpiration;

    /**套餐状态
	*/
    @ExcelProperty("套餐状态")
    private String packagestate;

    /**套餐状态修改日期
	*/
    private Date packagestatemodifiedon;

    /**本月套餐总量
	*/
    private BigDecimal packagecount=BigDecimal.ZERO;

    /**是否加入流量池
	*/
    @ExcelProperty("是否加入流量池")
    private int isflowpool;

    /**导入时间
	*/
    private Date importtime;

    /**是否同步流向
	*/
    private int issync=1;

    /**流向
	*/
    private String bindingstate="0";

    /**维护人
	*/
    private String maintainer;

    /**登记日期
	*/
    private Date registrationdate;

    /**设备ID
	*/
    private String equipmentid;

    /**通讯地址
	*/
    private String mailingid;

    /**主站端口
	*/
    private String serverport;

    /**产品名称
	*/
    private String productname;

    /**产品类型	故指/低电压/负荷
	*/
    private String producttype;

    /**来源id	云主站ID
	*/
    private String sourceid;

    /**来源类型	故指/低电压/负荷
	*/
    private String sourcetype;

    /**省区编码
	*/
    private String deptcode;

    /**省区名称
	*/
    private String deptname;

    /**服务状态
	*/
    private String servicestate;

    /**
    * @return the  iccid:ICCID
    */
    public String getIccid() {
        return this.iccid;
    }

    /**
    * @param iccid:ICCID
    */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
    * @return the  msisdn:MSISDN
    */
    public String getMsisdn() {
        return this.msisdn;
    }

    /**
    * @param msisdn:MSISDN
    */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
    * @return the  imsi:IMSI
    */
    public String getImsi() {
        return this.imsi;
    }

    /**
    * @param imsi:IMSI
    */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
    * @return the  platformtype:平台类型
    */
    public String getPlatformtype() {
        return this.platformtype;
    }

    /**
    * @param platformtype:平台类型
    */
    public void setPlatformtype(String platformtype) {
        this.platformtype = platformtype;
    }

    /**
    * @return the  openingdate:费用/资产/代采购/外包
    */
    public Date getOpeningdate() {
        return this.openingdate;
    }

    /**
    * @param openingdate:费用/资产/代采购/外包
    */
    public void setOpeningdate(Date openingdate) {
        this.openingdate = openingdate;
    }

    /**
    * @return the  holder:开户人
    */
    public String getHolder() {
        return this.holder;
    }

    /**
    * @param holder:开户人
    */
    public void setHolder(String holder) {
        this.holder = holder;
    }

    /**
    * @return the  activationdate:激活日期
    */
    public Date getActivationdate() {
        return this.activationdate;
    }

    /**
    * @param activationdate:激活日期
    */
    public void setActivationdate(Date activationdate) {
        this.activationdate = activationdate;
    }

    /**
    * @return the  state:卡状态
    */
    public String getState() {
        return this.state;
    }

    /**
    * @param state:卡状态
    */
    public void setState(String state) {
        this.state = state;
    }

    /**
    * @return the  onelinkstate:中移物联卡状态
    */
    public String getOnelinkstate() {
        return this.onelinkstate;
    }

    /**
    * @param onelinkstate:中移物联卡状态
    */
    public void setOnelinkstate(String onelinkstate) {
        this.onelinkstate = onelinkstate;
    }

    /**
    * @return the  statemodifiedon:卡状态修改日期
    */
    public Date getStatemodifiedon() {
        return this.statemodifiedon;
    }

    /**
    * @param statemodifiedon:卡状态修改日期
    */
    public void setStatemodifiedon(Date statemodifiedon) {
        this.statemodifiedon = statemodifiedon;
    }

    /**
    * @return the  packagelife:套餐年限
    */
    public String getPackagelife() {
        return this.packagelife;
    }

    /**
    * @param packagelife:套餐年限
    */
    public void setPackagelife(String packagelife) {
        this.packagelife = packagelife;
    }

    /**
    * @return the  packagetype:套餐类型
    */
    public String getPackagetype() {
        return this.packagetype;
    }

    /**
    * @param packagetype:套餐类型
    */
    public void setPackagetype(String packagetype) {
        this.packagetype = packagetype;
    }

    /**
    * @return the  packageexpiration:套餐到期时间
    */
    public Date getPackageexpiration() {
        return this.packageexpiration;
    }

    /**
    * @param packageexpiration:套餐到期时间
    */
    public void setPackageexpiration(Date packageexpiration) {
        this.packageexpiration = packageexpiration;
    }

    /**
    * @return the  packagestate:套餐状态
    */
    public String getPackagestate() {
        return this.packagestate;
    }

    /**
    * @param packagestate:套餐状态
    */
    public void setPackagestate(String packagestate) {
        this.packagestate = packagestate;
    }

    /**
    * @return the  packagestatemodifiedon:套餐状态修改日期
    */
    public Date getPackagestatemodifiedon() {
        return this.packagestatemodifiedon;
    }

    /**
    * @param packagestatemodifiedon:套餐状态修改日期
    */
    public void setPackagestatemodifiedon(Date packagestatemodifiedon) {
        this.packagestatemodifiedon = packagestatemodifiedon;
    }

    /**
    * @return the  packagecount:本月套餐总量
    */
    public BigDecimal getPackagecount() {
        return this.packagecount;
    }

    /**
    * @param packagecount:本月套餐总量
    */
    public void setPackagecount(BigDecimal packagecount) {
        this.packagecount = packagecount;
    }

    /**
    * @return the  isflowpool:是否加入流量池
    */
    public int getIsflowpool() {
        return this.isflowpool;
    }

    /**
    * @param isflowpool:是否加入流量池
    */
    public void setIsflowpool(int isflowpool) {
        this.isflowpool = isflowpool;
    }

    /**
    * @return the  importtime:导入时间
    */
    public Date getImporttime() {
        return this.importtime;
    }

    /**
    * @param importtime:导入时间
    */
    public void setImporttime(Date importtime) {
        this.importtime = importtime;
    }

    /**
    * @return the  issync:是否同步流向
    */
    public int getIssync() {
        return this.issync;
    }

    /**
    * @param issync:是否同步流向
    */
    public void setIssync(int issync) {
        this.issync = issync;
    }

    /**
    * @return the  bindingstate:流向
    */
    public String getBindingstate() {
        return this.bindingstate;
    }

    /**
    * @param bindingstate:流向
    */
    public void setBindingstate(String bindingstate) {
        this.bindingstate = bindingstate;
    }

    /**
    * @return the  maintainer:维护人
    */
    public String getMaintainer() {
        return this.maintainer;
    }

    /**
    * @param maintainer:维护人
    */
    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    /**
    * @return the  registrationdate:登记日期
    */
    public Date getRegistrationdate() {
        return this.registrationdate;
    }

    /**
    * @param registrationdate:登记日期
    */
    public void setRegistrationdate(Date registrationdate) {
        this.registrationdate = registrationdate;
    }

    /**
    * @return the  equipmentid:设备ID
    */
    public String getEquipmentid() {
        return this.equipmentid;
    }

    /**
    * @param equipmentid:设备ID
    */
    public void setEquipmentid(String equipmentid) {
        this.equipmentid = equipmentid;
    }

    /**
    * @return the  mailingid:通讯地址
    */
    public String getMailingid() {
        return this.mailingid;
    }

    /**
    * @param mailingid:通讯地址
    */
    public void setMailingid(String mailingid) {
        this.mailingid = mailingid;
    }

    /**
    * @return the  serverport:主站端口
    */
    public String getServerport() {
        return this.serverport;
    }

    /**
    * @param serverport:主站端口
    */
    public void setServerport(String serverport) {
        this.serverport = serverport;
    }

    /**
    * @return the  productname:产品名称
    */
    public String getProductname() {
        return this.productname;
    }

    /**
    * @param productname:产品名称
    */
    public void setProductname(String productname) {
        this.productname = productname;
    }

    /**
    * @return the  producttype:故指/低电压/负荷
    */
    public String getProducttype() {
        return this.producttype;
    }

    /**
    * @param producttype:故指/低电压/负荷
    */
    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    /**
    * @return the  sourceid:云主站ID
    */
    public String getSourceid() {
        return this.sourceid;
    }

    /**
    * @param sourceid:云主站ID
    */
    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }

    /**
    * @return the  sourcetype:故指/低电压/负荷
    */
    public String getSourcetype() {
        return this.sourcetype;
    }

    /**
    * @param sourcetype:故指/低电压/负荷
    */
    public void setSourcetype(String sourcetype) {
        this.sourcetype = sourcetype;
    }

    /**
    * @return the  deptcode:省区编码
    */
    public String getDeptcode() {
        return this.deptcode;
    }

    /**
    * @param deptcode:省区编码
    */
    public void setDeptcode(String deptcode) {
        this.deptcode = deptcode;
    }

    /**
    * @return the  deptname:省区名称
    */
    public String getDeptname() {
        return this.deptname;
    }

    /**
    * @param deptname:省区名称
    */
    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    /**
    * @return the  servicestate:服务状态
    */
    public String getServicestate() {
        return this.servicestate;
    }

    /**
    * @param servicestate:服务状态
    */
    public void setServicestate(String servicestate) {
        this.servicestate = servicestate;
    }

}
