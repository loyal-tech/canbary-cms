package com.adopt.apigw.modules.Voucher.domain;

import com.adopt.apigw.modules.Voucher.module.Auditable;
import com.adopt.apigw.modules.Voucher.module.VoucherStatus;
import com.adopt.apigw.modules.VoucherBatch.domain.BSSVoucherBatch;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLTVOUCHER")
public class Voucher extends Auditable<Long>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
	@JoinColumn(name = "voucher_batch_id")
	@NotFound(action = NotFoundAction.IGNORE)
	private BSSVoucherBatch voucherBatch;

	@Column(name = "CODE")
	private String code;

	@Column(name = "BATCH_NAME")
	private String batchName;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private VoucherStatus status;

	@DiffIgnore
	@ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;

	@DiffIgnore
	@ApiModelProperty(notes = "This is buid", required = true)
	@Column (name="buid")
	private Long buId;

	@DiffIgnore
	@ApiModelProperty(notes = "this is a date for voucher used" , required = true)
	@Column(name = "voucher_used_date")
	private LocalDateTime voucherUsedDate;
	@DiffIgnore
	@Column(name = "createdbystaffid")
	private Integer createdByStaffId;

	@Column(name = "serialNumber")
	private String serial_number;

}
