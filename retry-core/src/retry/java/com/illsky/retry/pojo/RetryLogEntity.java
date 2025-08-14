/** 
* All Rights Reserved , Copyright (C) 2023 , 青岛鼎信通讯股份有限公司
* 
* RetryLogEntity
* SIM卡接口传输日志
* 
* 修改纪录
* 2023-05-18 版本：1.0 sucongcong 创建。
* @version 版本：1.0
* @author 作者：sucongcong</name>
* 创建日期2023-05-18</date>
*/

package com.illsky.retry.pojo;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import java.util.Date;
@Data
@TableName(value= "retrylog")
public class RetryLogEntity {

    private static final long serialVersionUID = 1L;
    

    /** TableName:SIM卡接口传输日志
	*/
    public static String tableName= "retrylog";

    /**哈希值	class_name+method_name+method_param_values
	*/
    private String digest;

    /**单据类型	
	*/
    private String sourcetype;

    /**单据编号	
	*/
    private String sourceid;

    /**接口地址	
	*/
    private String url;

    /**操作类型	
	*/
    private String category;

    /**实例名	
	*/
    private String newinstancename;

    /**全类名	
	*/
    private String classname;

    /**方法名	
	*/
    private String methodname;

    /**方法入参	
	*/
    private String paramvalues;

    /**重试次数	
	*/
    private int retrycount;

    /**最大重试次数	
	*/
    private int maxretrycount;

    /**返回值	
	*/
    private String responsemsg;

    /**错误码	
	*/
    private String errorcode;

    /**错误信息	
	*/
    private String errormsg;

    /**单据日期	
	*/
    private Date currentdate;

    /**重试有效期	
	*/
    private Date retryexpirydate;

    /**处理状态	0：未解决；1：已解决
	*/
    private int status;

    private int sortcode;
    private String description;
    private int enabled;
    private long createuser;
    private long modifieduser;
    private long createorgid;
    private long id;
    private Date createon;
    private Date modifiedon;


    public static String FieldId = "id";
    /** ColumnName:哈希值	class_name+method_name+method_param_values
     */
    public static String FieldDigest = "digest";

    /** ColumnName:单据类型
     */
    public static String FieldSourcetype = "sourcetype";

    /** ColumnName:单据编号
     */
    public static String FieldSourceid = "sourceid";

    /** ColumnName:接口地址
     */
    public static String FieldUrl = "url";

    /** ColumnName:操作类型
     */
    public static String FieldCategory = "category";

    /** ColumnName:实例名
     */
    public static String FieldNewinstancename = "newinstancename";

    /** ColumnName:全类名
     */
    public static String FieldClassname = "classname";

    /** ColumnName:方法名
     */
    public static String FieldMethodname = "methodname";

    /** ColumnName:方法入参
     */
    public static String FieldParamvalues = "paramvalues";

    /** ColumnName:重试次数
     */
    public static String FieldRetrycount = "retrycount";

    /** ColumnName:最大重试次数
     */
    public static String FieldMaxretrycount = "maxretrycount";

    /** ColumnName:返回值
     */
    public static String FieldResponsemsg = "responsemsg";

    /** ColumnName:错误码
     */
    public static String FieldErrorcode = "errorcode";

    /** ColumnName:错误信息
     */
    public static String FieldErrormsg = "errormsg";

    /** ColumnName:单据日期
     */
    public static String FieldCurrentdate = "currentdate";

    /** ColumnName:重试有效期
     */
    public static String FieldRetryexpirydate = "retryexpirydate";

    /** ColumnName:处理状态	0：未解决；1：已解决
     */
    public static String FieldStatus = "status";

    public static String FieldModifiedon = "modifiedon";





}
