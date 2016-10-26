package magia.af.ezpay.Parser;

import java.io.Serializable;

public class RSSItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long _status = null;
	private Long _payId = null;
	private Long _billId = null;
	private Long _amount = null;
	private Long _telNo = null;
	private Long _payDate = null;


	private String _message = null;

	private String _StatusVolume = null;
	private String _StatusDuration = null;
	private String _StatusBandwidth = null;
	private String _StatusPaidtype = null;

//	"errmsg":[{"volume":"1000GB","duration":"12Month","serviceType":"Internet","price":"981000","packId":"10118"}]}
	private String _ListDuration  = null;
	private String _ListVolume  = null;
	private String _ListPrice = null;
	private String _ListPackId  = null;



	private boolean _isPayed = false;
	private boolean _isInDb = false;
	private boolean _AdslActive=false;
	private boolean _ListActive=false;
	private boolean _inDiagram = false;


	public boolean is_ListActive() {
		return _ListActive;
	}

	public void set_ListActive(boolean _ListActive) {
		this._ListActive = _ListActive;
	}

	public String get_ListDuration() {
		return _ListDuration;
	}

	public void set_ListDuration(String _ListDuration) {
		this._ListDuration = _ListDuration;
	}

	public String get_ListVolume() {
		return _ListVolume;
	}

	public void set_ListVolume(String _ListVolume) {
		this._ListVolume = _ListVolume;
	}


	public String get_StatusPaidtype() {
		return _StatusPaidtype;
	}

	public void set_StatusPaidtype(String _StatusPaidtype) {
		this._StatusPaidtype = _StatusPaidtype;
	}



	public boolean is_inDiagram() {
		return _inDiagram;
	}

	public void set_inDiagram(boolean _inDiagram) {
		this._inDiagram = _inDiagram;
	}

	public String get_StatusBandwidth() {
		return _StatusBandwidth;
	}

	public void set_StatusBandwidth(String _StatusBandwidth) {
		this._StatusBandwidth = _StatusBandwidth;
	}

	public String get_StatusVolume() {
		return _StatusVolume;
	}

	public String get_StatusDuration() {
		return _StatusDuration;
	}

	public void set_StatusDuration(String _StatusDuration) {
		this._StatusDuration = _StatusDuration;
	}

	public String get_ListPrice() {
		return _ListPrice;
	}

	public void set_ListPrice(String _AdslPrice) {
		this._ListPrice = _AdslPrice;
	}

	public String get_ListPackId() {
		return _ListPackId;
	}

	public void set_ListPackId(String _ListPackId) {
		this._ListPackId = _ListPackId;
	}

	public void set_StatusVolume(String _AdslData) {
		this._StatusVolume = _AdslData;
	}

	public void setStatus(Long status) {
		_status = status;
	}

	public boolean is_AdslActive() {
		return _AdslActive;
	}

	public void set_AdslActive(boolean _isAdslActive) {
		this._AdslActive = _isAdslActive;
	}

	public void setPayDate(Long date) {

		_payDate = date;
	}

	public boolean get_isInDb() {
		return _isInDb;
	}

	public void set_isInDb(boolean _isInDb) {
		this._isInDb = _isInDb;
	}

	public void setIsPayed(boolean isPayed) {

		_isPayed = isPayed;
	}


	public void setPayId(Long payId) {
		_payId = payId;
	}

	public void setBillId(Long billId) {
		_billId = billId;
	}

	public void setAmount(Long amount) {
		_amount = amount;
	}

	public void setMessage(String message) {
		_message = message;
	}

	public void setTelNo(Long telNo) {
		_telNo = telNo;
	}

	public Long getTelNo() {
		return _telNo;
	}
	
	public String getMessage() {
		return _message;
	}
	
	public Long getAmount() {
		return _amount;
	}

	public Long getPayDate() {

		return _payDate;
	}

	public boolean getIsPayed() {

		return _isPayed;
	}

	public Long getBillId() {
		return _billId;
	}

	public Long getPayId() {
		return _payId;
	}

	public Long getStatus() {
		return _status;
	}

}
