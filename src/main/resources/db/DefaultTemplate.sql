
insert into tbltemplatemanagement(templatename,templatetype,status) values
('PaymentReceipt','Payment','A');

update tbltemplatemanagement set jrxmlfile=
'
<?xml version="1.0" encoding="UTF-8"?>
<!-- Created with Jaspersoft Studio version 6.13.0.final using JasperReports Library version 6.13.0-46ada4d1be8f3c5985fd0b6146f3ed44caed6f05  -->
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd" name="books" pageWidth="595" pageHeight="842" whenNoDataType="AllSectionsNoDetail" columnWidth="535" leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20" uuid="a162276b-35be-405f-b3ff-18f7d8feadb7">
	<property name="template.engine" value="tabular_template"/>
	<property name="com.jaspersoft.studio.data.defaultdataadapter" value="DataAdapter.xml"/>
	<property name="ireport.zoom" value="1.0"/>
	<property name="ireport.x" value="58"/>
	<property name="ireport.y" value="174"/>
	<style name="Table_TH" mode="Opaque" backcolor="#066990">
		<box>
			<topPen lineWidth="0.5" lineColor="#000000"/>
			<bottomPen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<style name="Table_CH" mode="Opaque" forecolor="#FFFFFF" backcolor="#06618F" hTextAlign="Center" fontSize="12">
		<box>
			<topPen lineWidth="0.5" lineColor="#000000"/>
			<bottomPen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<style name="Table_TD" mode="Opaque" backcolor="#FFFFFF" hTextAlign="Center">
		<box>
			<topPen lineWidth="0.5" lineColor="#000000"/>
			<bottomPen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<subDataset name="ChargeList" uuid="38b6f16c-4460-4ddd-b4e5-173b76dd6fa8">
		<field name="name" class="java.lang.String"/>
		<field name="price" class="java.lang.Double"/>
		<field name="tax" class="java.lang.Double"/>
		<field name="total" class="java.lang.Double"/>
		<field name="discount" class="java.lang.Double"/>
	</subDataset>
	<queryString language="XPath">
		<![CDATA[/invoice]]>
	</queryString>
	<field name="id" class="java.lang.String">
		<fieldDescription><![CDATA[id]]></fieldDescription>
	</field>
	<field name="number" class="java.lang.String">
		<fieldDescription><![CDATA[number]]></fieldDescription>
	</field>
	<field name="customerId" class="java.lang.String">
		<fieldDescription><![CDATA[customerId]]></fieldDescription>
	</field>
	<field name="billDate" class="java.lang.String">
		<fieldDescription><![CDATA[billDate]]></fieldDescription>
	</field>
	<field name="createDate" class="java.lang.String">
		<fieldDescription><![CDATA[createDate]]></fieldDescription>
	</field>
	<field name="startDate" class="java.lang.String">
		<fieldDescription><![CDATA[startDate]]></fieldDescription>
	</field>
	<field name="endDate" class="java.lang.String">
		<fieldDescription><![CDATA[endDate]]></fieldDescription>
	</field>
	<field name="dueDate" class="java.lang.String">
		<fieldDescription><![CDATA[dueDate]]></fieldDescription>
	</field>
	<field name="latePaymentDate" class="java.lang.String">
		<fieldDescription><![CDATA[latePaymentDate]]></fieldDescription>
	</field>
	<field name="charge" class="java.lang.String">
		<fieldDescription><![CDATA[charge]]></fieldDescription>
	</field>
	<field name="tax" class="java.lang.String">
		<fieldDescription><![CDATA[tax]]></fieldDescription>
	</field>
	<field name="discount" class="java.lang.String">
		<fieldDescription><![CDATA[discount]]></fieldDescription>
	</field>
	<field name="usage" class="java.lang.String">
		<fieldDescription><![CDATA[usage]]></fieldDescription>
	</field>
	<field name="total" class="java.lang.String">
		<fieldDescription><![CDATA[total]]></fieldDescription>
	</field>
	<field name="previousBalance" class="java.lang.String">
		<fieldDescription><![CDATA[previousBalance]]></fieldDescription>
	</field>
	<field name="latePaymentFee" class="java.lang.String">
		<fieldDescription><![CDATA[latePaymentFee]]></fieldDescription>
	</field>
	<field name="currentPayment" class="java.lang.String">
		<fieldDescription><![CDATA[currentPayment]]></fieldDescription>
	</field>
	<field name="currentDebit" class="java.lang.String">
		<fieldDescription><![CDATA[currentDebit]]></fieldDescription>
	</field>
	<field name="currentCredit" class="java.lang.String">
		<fieldDescription><![CDATA[currentCredit]]></fieldDescription>
	</field>
	<field name="totalDue" class="java.lang.String">
		<fieldDescription><![CDATA[totalDue]]></fieldDescription>
	</field>
	<field name="totalAmountInWords" class="java.lang.String">
		<fieldDescription><![CDATA[totalAmountInWords]]></fieldDescription>
	</field>
	<field name="totalDueInWords" class="java.lang.String">
		<fieldDescription><![CDATA[totalDueInWords]]></fieldDescription>
	</field>
	<field name="accountnumber" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/accountnumber]]></fieldDescription>
	</field>
	<field name="email" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/email]]></fieldDescription>
	</field>
	<field name="firstname" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/firstname]]></fieldDescription>
	</field>
	<field name="lastname" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/lastname]]></fieldDescription>
	</field>
	<field name="name" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/name]]></fieldDescription>
	</field>
	<field name="phone" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/phone]]></fieldDescription>
	</field>
	<field name="subscriberpackage" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/subscriberpackage]]></fieldDescription>
	</field>
	<field name="subscriberpackageid" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/subscriberpackageid]]></fieldDescription>
	</field>
	<field name="address1" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/address1]]></fieldDescription>
	</field>
	<field name="address2" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/address2]]></fieldDescription>
	</field>
	<field name="addresstype" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/addresstype]]></fieldDescription>
	</field>
	<field name="city" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/city]]></fieldDescription>
	</field>
	<field name="country" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/country]]></fieldDescription>
	</field>
	<field name="pincode" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/pincode]]></fieldDescription>
	</field>
	<field name="state" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/state]]></fieldDescription>
	</field>
	<field name="alias" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/alias]]></fieldDescription>
	</field>
	<field name="description" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/description]]></fieldDescription>
	</field>
	<field name="downloadqos" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/downloadqos]]></fieldDescription>
	</field>
	<field name="enddate" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/enddate]]></fieldDescription>
	</field>
	<field name="planname" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/name]]></fieldDescription>
	</field>
	<field name="postpaidplanid" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/postpaidplanid]]></fieldDescription>
	</field>
	<field name="quota" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/quota]]></fieldDescription>
	</field>
	<field name="quotaunit" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/quotaunit]]></fieldDescription>
	</field>
	<field name="status" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/status]]></fieldDescription>
	</field>
	<field name="uploadqos" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/uploadqos]]></fieldDescription>
	</field>
	<field name="laststatuschange" class="java.lang.String">
		<fieldDescription><![CDATA[laststatuschange]]></fieldDescription>
	</field>
	<field name="invoicelist" class="java.lang.String"/>
	<title>
		<band height="416">
			<rectangle>
				<reportElement x="280" y="42" width="275" height="208" forecolor="#1B56E0" uuid="6b5490f1-ada5-4d3f-943d-da628a310af7"/>
			</rectangle>
			<frame>
				<reportElement mode="Opaque" x="-20" y="-20" width="595" height="50" backcolor="#1B56E0" uuid="333d5d27-232f-4002-931f-2ea09a376000"/>
				<image scaleImage="RetainShape">
					<reportElement x="20" y="10" width="555" height="30" forecolor="#FFFFFF" uuid="9b6cb13d-a79b-4035-9a8c-4e1015eb823a"/>
					<imageExpression><![CDATA["C://logo_new.png"]]></imageExpression>
				</image>
				<image scaleImage="RetainShape">
					<reportElement x="520" y="10" width="59" height="30" forecolor="#FFFFFF" uuid="9b6cb13d-a79b-4035-9a8c-4e1015eb823a"/>
					<imageExpression><![CDATA["C://logo_new.png"]]></imageExpression>
				</image>
			</frame>
			<rectangle>
				<reportElement x="-3" y="42" width="273" height="208" forecolor="#1B56E0" uuid="43a8d4cf-6f4b-4411-8513-d359e1542065"/>
			</rectangle>
			<textField>
				<reportElement x="3" y="50" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Name : "+ $F{name})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="75" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Address : "+ $F{address1})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="100" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[$F{address2}]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="130" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("City : "+ $F{city})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="160" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("State : "+ $F{state})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="187" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Country : "+ $F{country})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="213" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Pincode : "+ $F{pincode})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="285" y="50" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Bill No # "+ $F{number})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="285" y="75" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Customer Id : "+ $F{customerId})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="285" y="100" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Bill Date : "+ $F{billDate})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="285" y="153" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Start Date : "+ $F{startDate})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="285" y="183" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("End Date : "+ $F{endDate})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="285" y="128" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Due Date : "+ $F{dueDate})]]></textFieldExpression>
			</textField>
			<frame>
				<reportElement x="-3" y="261" width="558" height="149" uuid="935a8d6d-0a9d-4161-af59-efbd680774b1"/>
				<componentElement>
					<reportElement x="0" y="0" width="558" height="149" isRemoveLineWhenBlank="true" forecolor="#1A3CD6" backcolor="#FFFFFF" uuid="5ec0151d-12ee-4fb6-87f6-b4c42c34e84e">
						<property name="com.jaspersoft.studio.layout" value="com.jaspersoft.studio.editor.layout.VerticalRowLayout"/>
						<property name="com.jaspersoft.studio.table.style.table_header" value="Table_TH"/>
						<property name="com.jaspersoft.studio.table.style.column_header" value="Table_CH"/>
						<property name="com.jaspersoft.studio.table.style.detail" value="Table_TD"/>
						<property name="net.sf.jasperreports.export.headertoolbar.table.name" value=""/>
						<property name="com.jaspersoft.studio.components.autoresize.proportional" value="true"/>
					</reportElement>
					<jr:table xmlns:jr="http://jasperreports.sourceforge.net/jasperreports/components" xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports/components http://jasperreports.sourceforge.net/xsd/components.xsd">
						<datasetRun subDataset="ChargeList" uuid="fd6c5596-fbcc-479d-a96e-0836a7a08797">
							<dataSourceExpression><![CDATA[((net.sf.jasperreports.engine.data.JRXmlDataSource)$P{REPORT_DATA_SOURCE}).subDataSource("invoice/invoiceDetail/invoiceList")]]></dataSourceExpression>
						</datasetRun>
						<jr:column width="177" uuid="139d7312-c6b8-48a1-995b-e48ad772fd0f">
							<jr:columnHeader style="Table_CH" height="30">
								<staticText>
									<reportElement x="0" y="0" width="177" height="30" forecolor="#FFFFFF" uuid="b962ab9c-2ddd-492b-9543-9dafe4b6aed4"/>
									<box>
										<pen lineColor="#FFFFFF"/>
									</box>
									<textElement textAlignment="Center" verticalAlignment="Middle">
										<font fontName="SansSerif" size="12" isBold="true"/>
									</textElement>
									<text><![CDATA[Charge Name]]></text>
								</staticText>
							</jr:columnHeader>
							<jr:detailCell style="Table_TD" height="30">
								<textField>
									<reportElement x="0" y="0" width="177" height="30" uuid="d482e827-fda9-4cf3-83fb-b6ea5d4ce81b"/>
									<textElement textAlignment="Center" verticalAlignment="Middle">
										<font fontName="SansSerif" size="12"/>
									</textElement>
									<textFieldExpression><![CDATA[$F{name}]]></textFieldExpression>
								</textField>
							</jr:detailCell>
						</jr:column>
						<jr:column width="93" uuid="65204efa-31dc-4b96-9c33-2255d58e6e76">
							<jr:columnHeader style="Table_CH" height="30">
								<staticText>
									<reportElement x="0" y="0" width="93" height="30" forecolor="#FFFFFF" uuid="84f261f4-5b91-4df6-954e-817231adb7e3"/>
									<textElement textAlignment="Center" verticalAlignment="Middle">
										<font fontName="SansSerif" size="12" isBold="true"/>
									</textElement>
									<text><![CDATA[Base Price]]></text>
								</staticText>
							</jr:columnHeader>
							<jr:detailCell style="Table_TD" height="30">
								<textField>
									<reportElement x="0" y="0" width="93" height="30" uuid="e19a4c86-891e-4c1f-8903-b27e16d52621"/>
									<textElement textAlignment="Center" verticalAlignment="Middle">
										<font size="12"/>
									</textElement>
									<textFieldExpression><![CDATA[$F{price}]]></textFieldExpression>
								</textField>
							</jr:detailCell>
						</jr:column>
						<jr:column width="93" uuid="65204efa-31dc-4b96-9c33-2255d58e6e76">
							<jr:columnHeader style="Table_CH" height="30">
								<staticText>
									<reportElement x="0" y="0" width="93" height="30" forecolor="#FFFFFF" uuid="84f261f4-5b91-4df6-954e-817231adb7e3"/>
									<textElement textAlignment="Center" verticalAlignment="Middle">
										<font fontName="SansSerif" size="12" isBold="true"/>
									</textElement>
									<text><![CDATA[Discount]]></text>
								</staticText>
							</jr:columnHeader>
							<jr:detailCell style="Table_TD" height="30">
								<textField>
									<reportElement x="0" y="0" width="93" height="30" uuid="e19a4c86-891e-4c1f-8903-b27e16d52621"/>
									<textElement textAlignment="Center" verticalAlignment="Middle">
										<font size="12"/>
									</textElement>
									<textFieldExpression><![CDATA[$F{discount}]]></textFieldExpression>
								</textField>
							</jr:detailCell>
						</jr:column>
						<jr:column width="93" uuid="65204efa-31dc-4b96-9c33-2255d58e6e76">
							<jr:columnHeader style="Table_CH" height="30">
								<staticText>
									<reportElement x="0" y="0" width="93" height="30" forecolor="#FFFFFF" uuid="84f261f4-5b91-4df6-954e-817231adb7e3"/>
									<textElement textAlignment="Center" verticalAlignment="Middle">
										<font fontName="SansSerif" size="12" isBold="true"/>
									</textElement>
									<text><![CDATA[Tax]]></text>
								</staticText>
							</jr:columnHeader>
							<jr:detailCell style="Table_TD" height="30">
								<textField>
									<reportElement x="0" y="0" width="93" height="30" uuid="e19a4c86-891e-4c1f-8903-b27e16d52621"/>
									<textElement textAlignment="Center" verticalAlignment="Middle">
										<font size="12"/>
									</textElement>
									<textFieldExpression><![CDATA[$F{tax}]]></textFieldExpression>
								</textField>
							</jr:detailCell>
						</jr:column>
						<jr:column width="103" uuid="d1fd425e-a190-42c1-9284-4567f09e60df">
							<jr:columnHeader style="Table_CH" height="30">
								<staticText>
									<reportElement x="0" y="0" width="103" height="30" forecolor="#FFFFFF" uuid="f4d523d7-5533-4c58-89d1-8ffd042e0af1"/>
									<textElement textAlignment="Center" verticalAlignment="Middle">
										<font fontName="SansSerif" size="12" isBold="true"/>
									</textElement>
									<text><![CDATA[Total Amount]]></text>
								</staticText>
							</jr:columnHeader>
							<jr:detailCell style="Table_TD" height="30">
								<textField isBlankWhenNull="false">
									<reportElement x="0" y="0" width="103" height="30" forecolor="#000000" uuid="dbaa9bf5-58f3-4855-9aa4-27990b1043c7"/>
									<textElement textAlignment="Center" verticalAlignment="Middle">
										<font size="12"/>
									</textElement>
									<textFieldExpression><![CDATA[$F{total}]]></textFieldExpression>
								</textField>
							</jr:detailCell>
						</jr:column>
					</jr:table>
				</componentElement>
			</frame>
		</band>
	</title>
	<detail>
		<band height="304">
			<rectangle>
				<reportElement x="-3" y="10" width="558" height="150" forecolor="#1B56E0" uuid="800e0b0b-0c92-4dc7-a7f3-d6d41a6bb980"/>
			</rectangle>
			<textField>
				<reportElement x="230" y="29" width="314" height="20" uuid="6856bd16-8511-469d-897e-228e48ecaefa"/>
				<textElement textAlignment="Right">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Total Discount : Rs."+ $F{discount})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="230" y="59" width="314" height="20" uuid="69b91147-cd37-4ed3-8e21-681fb6749878"/>
				<textElement textAlignment="Right">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Total Tax : Rs."+ $F{tax})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="230" y="90" width="315" height="20" uuid="82636d0b-8317-4701-bd9c-6236ae7bfc7e"/>
				<textElement textAlignment="Right">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Previous Due/Balance : Rs."+ $F{previousBalance})]]></textFieldExpression>
			</textField>
			<rectangle>
				<reportElement x="-2" y="185" width="558" height="30" forecolor="#1B56E0" uuid="e2edfe46-eab5-4ad1-a44d-d0e1db40f821"/>
			</rectangle>
			<textField>
				<reportElement x="5" y="190" width="542" height="20" uuid="6891aaba-41ca-49f1-bcc8-1cdc357ce21a"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Total Due in Words : "+ $F{totalAmountInWords})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="230" y="125" width="315" height="20" uuid="0ae15fe1-785b-433a-9ace-ef2b64656d38"/>
				<textElement textAlignment="Right">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Total Amount Due : Rs."+ $F{totalDue})]]></textFieldExpression>
			</textField>
			<rectangle>
				<reportElement x="-2" y="10" width="222" height="150" forecolor="#1B56E0" uuid="b628846b-5f4f-4080-ab2e-3ae855323621"/>
			</rectangle>
			<staticText>
				<reportElement x="6" y="24" width="205" height="20" uuid="a53bf401-665a-47fd-ad7c-441c40577ae9"/>
				<textElement textAlignment="Center">
					<font size="13" isBold="true"/>
				</textElement>
				<text><![CDATA[Plan Information]]></text>
			</staticText>
			<textField>
				<reportElement x="4" y="59" width="205" height="20" uuid="d820a682-4cc7-4db7-900e-8b7633e8e50d"/>
				<textElement textAlignment="Center">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Name : "+ $F{planname})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="5" y="92" width="205" height="20" uuid="df2ed508-9144-4c71-827e-a34f5c04f2c4"/>
				<textElement textAlignment="Center">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Quota : "+ $F{quota} + " " + $F{quotaunit})]]></textFieldExpression>
			</textField>
		</band>
	</detail>
	<pageFooter>
		<band height="30">
			<textField>
				<reportElement mode="Opaque" x="0" y="0" width="515" height="13" backcolor="#E6E6E6" uuid="b8f7a38f-7c55-4e00-8050-80904948a04a"/>
				<textElement textAlignment="Right"/>
				<textFieldExpression><![CDATA["Page "+$V{PAGE_NUMBER}+" of"]]></textFieldExpression>
			</textField>
			<textField evaluationTime="Report">
				<reportElement mode="Opaque" x="515" y="0" width="40" height="13" backcolor="#E6E6E6" uuid="2114186e-67d4-4f5b-bd1a-23b37d669c29"/>
				<textFieldExpression><![CDATA[" " + $V{PAGE_NUMBER}]]></textFieldExpression>
			</textField>
		</band>
	</pageFooter>
</jasperReport>
';




insert into tbltemplatemanagement(templatename,templatetype,status) values
('PartnerComission','Comission','A');

update tbltemplatemanagement set jrxmlfile=
'
<?xml version="1.0" encoding="UTF-8"?>
<!-- Created with Jaspersoft Studio version 6.13.0.final using JasperReports Library version 6.13.0-46ada4d1be8f3c5985fd0b6146f3ed44caed6f05  -->
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd" name="books" pageWidth="595" pageHeight="842" whenNoDataType="AllSectionsNoDetail" columnWidth="535" leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20" uuid="a162276b-35be-405f-b3ff-18f7d8feadb7">
	<property name="template.engine" value="tabular_template"/>
	<property name="com.jaspersoft.studio.data.defaultdataadapter" value="DataAdapter.xml"/>
	<property name="ireport.zoom" value="1.0"/>
	<property name="ireport.x" value="58"/>
	<property name="ireport.y" value="174"/>
	<style name="Table_TH" mode="Opaque" backcolor="#066990">
		<box>
			<topPen lineWidth="0.5" lineColor="#000000"/>
			<bottomPen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<style name="Table_CH" mode="Opaque" forecolor="#FFFFFF" backcolor="#06618F" hTextAlign="Center" fontSize="12">
		<box>
			<topPen lineWidth="0.5" lineColor="#000000"/>
			<bottomPen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<style name="Table_TD" mode="Opaque" backcolor="#FFFFFF" hTextAlign="Center">
		<box>
			<topPen lineWidth="0.5" lineColor="#000000"/>
			<bottomPen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<subDataset name="ChargeList" uuid="38b6f16c-4460-4ddd-b4e5-173b76dd6fa8">
		<field name="name" class="java.lang.String"/>
		<field name="price" class="java.lang.Double"/>
		<field name="tax" class="java.lang.Double"/>
		<field name="total" class="java.lang.Double"/>
		<field name="discount" class="java.lang.Double"/>
		<field name="comm_type" class="java.lang.String"/>
	</subDataset>
	<queryString language="XPath">
		<![CDATA[/invoice]]>
	</queryString>
	<field name="id" class="java.lang.String">
		<fieldDescription><![CDATA[id]]></fieldDescription>
	</field>
	<field name="number" class="java.lang.String">
		<fieldDescription><![CDATA[number]]></fieldDescription>
	</field>
	<field name="customerId" class="java.lang.String">
		<fieldDescription><![CDATA[customerId]]></fieldDescription>
	</field>
	<field name="billDate" class="java.lang.String">
		<fieldDescription><![CDATA[billDate]]></fieldDescription>
	</field>
	<field name="createDate" class="java.lang.String">
		<fieldDescription><![CDATA[createDate]]></fieldDescription>
	</field>
	<field name="startDate" class="java.lang.String">
		<fieldDescription><![CDATA[startDate]]></fieldDescription>
	</field>
	<field name="endDate" class="java.lang.String">
		<fieldDescription><![CDATA[endDate]]></fieldDescription>
	</field>
	<field name="dueDate" class="java.lang.String">
		<fieldDescription><![CDATA[dueDate]]></fieldDescription>
	</field>
	<field name="latePaymentDate" class="java.lang.String">
		<fieldDescription><![CDATA[latePaymentDate]]></fieldDescription>
	</field>
	<field name="charge" class="java.lang.String">
		<fieldDescription><![CDATA[charge]]></fieldDescription>
	</field>
	<field name="tax" class="java.lang.String">
		<fieldDescription><![CDATA[tax]]></fieldDescription>
	</field>
	<field name="discount" class="java.lang.String">
		<fieldDescription><![CDATA[discount]]></fieldDescription>
	</field>
	<field name="usage" class="java.lang.String">
		<fieldDescription><![CDATA[usage]]></fieldDescription>
	</field>
	<field name="total" class="java.lang.String">
		<fieldDescription><![CDATA[total]]></fieldDescription>
	</field>
	<field name="previousBalance" class="java.lang.String">
		<fieldDescription><![CDATA[previousBalance]]></fieldDescription>
	</field>
	<field name="latePaymentFee" class="java.lang.String">
		<fieldDescription><![CDATA[latePaymentFee]]></fieldDescription>
	</field>
	<field name="currentPayment" class="java.lang.String">
		<fieldDescription><![CDATA[currentPayment]]></fieldDescription>
	</field>
	<field name="currentDebit" class="java.lang.String">
		<fieldDescription><![CDATA[currentDebit]]></fieldDescription>
	</field>
	<field name="currentCredit" class="java.lang.String">
		<fieldDescription><![CDATA[currentCredit]]></fieldDescription>
	</field>
	<field name="totalDue" class="java.lang.String">
		<fieldDescription><![CDATA[totalDue]]></fieldDescription>
	</field>
	<field name="totalAmountInWords" class="java.lang.String">
		<fieldDescription><![CDATA[totalAmountInWords]]></fieldDescription>
	</field>
	<field name="totalDueInWords" class="java.lang.String">
		<fieldDescription><![CDATA[totalDueInWords]]></fieldDescription>
	</field>
	<field name="accountnumber" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/accountnumber]]></fieldDescription>
	</field>
	<field name="email" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/email]]></fieldDescription>
	</field>
	<field name="firstname" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/firstname]]></fieldDescription>
	</field>
	<field name="lastname" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/lastname]]></fieldDescription>
	</field>
	<field name="name" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/name]]></fieldDescription>
	</field>
	<field name="phone" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/phone]]></fieldDescription>
	</field>
	<field name="subscriberpackage" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/subscriberpackage]]></fieldDescription>
	</field>
	<field name="subscriberpackageid" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/subscriberpackageid]]></fieldDescription>
	</field>
	<field name="address1" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/address1]]></fieldDescription>
	</field>
	<field name="address2" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/address2]]></fieldDescription>
	</field>
	<field name="addresstype" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/addresstype]]></fieldDescription>
	</field>
	<field name="city" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/city]]></fieldDescription>
	</field>
	<field name="country" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/country]]></fieldDescription>
	</field>
	<field name="pincode" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/pincode]]></fieldDescription>
	</field>
	<field name="state" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/state]]></fieldDescription>
	</field>
	<field name="alias" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/alias]]></fieldDescription>
	</field>
	<field name="description" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/description]]></fieldDescription>
	</field>
	<field name="downloadqos" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/downloadqos]]></fieldDescription>
	</field>
	<field name="enddate" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/enddate]]></fieldDescription>
	</field>
	<field name="planname" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/name]]></fieldDescription>
	</field>
	<field name="displayname" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/displayname]]></fieldDescription>
	</field>
	<field name="postpaidplanid" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/postpaidplanid]]></fieldDescription>
	</field>
	<field name="quota" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/quota]]></fieldDescription>
	</field>
	<field name="quotaunit" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/quotaunit]]></fieldDescription>
	</field>
	<field name="status" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/status]]></fieldDescription>
	</field>
	<field name="uploadqos" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/uploadqos]]></fieldDescription>
	</field>
	<field name="laststatuschange" class="java.lang.String">
		<fieldDescription><![CDATA[laststatuschange]]></fieldDescription>
	</field>
	<field name="invoicelist" class="java.lang.String"/>
	<title>
		<band height="370">
			<rectangle>
				<reportElement x="280" y="42" width="275" height="318" forecolor="#4CB319" uuid="6b5490f1-ada5-4d3f-943d-da628a310af7"/>
			</rectangle>
			<frame>
				<reportElement mode="Opaque" x="-20" y="-20" width="595" height="50" backcolor="#4CB319" uuid="333d5d27-232f-4002-931f-2ea09a376000"/>
				<image scaleImage="RetainShape">
					<reportElement x="20" y="10" width="555" height="30" forecolor="#FFFFFF" uuid="9b6cb13d-a79b-4035-9a8c-4e1015eb823a"/>
					<imageExpression><![CDATA["C://logo_new.png"]]></imageExpression>
				</image>
				<image scaleImage="RetainShape">
					<reportElement x="520" y="10" width="59" height="30" forecolor="#FFFFFF" uuid="9b6cb13d-a79b-4035-9a8c-4e1015eb823a"/>
					<imageExpression><![CDATA["C://logo_new.png"]]></imageExpression>
				</image>
				<staticText>
					<reportElement x="180" y="10" width="220" height="30" forecolor="#FFFFFF" uuid="0658bd99-4ef5-4f51-b195-cdee9eb72334"/>
					<textElement textAlignment="Center">
						<font size="16" isBold="true" isUnderline="true"/>
					</textElement>
					<text><![CDATA[Commission Statement]]></text>
				</staticText>
			</frame>
			<rectangle>
				<reportElement x="-3" y="42" width="273" height="318" forecolor="#4CB319" uuid="43a8d4cf-6f4b-4411-8513-d359e1542065"/>
			</rectangle>
			<textField>
				<reportElement x="3" y="60" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Address : "+ $F{address1})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="89" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[$F{address2}]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="130" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("City : "+ $F{city})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="160" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("State : "+ $F{state})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="187" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Country : "+ $F{country})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="213" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Pincode : "+ $F{pincode})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="285" y="50" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Receipt No # "+ $F{number})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="287" y="130" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Partner Id : "+ $F{customerId})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="284" y="80" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Receipt Date : "+ $F{createDate})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="287" y="160" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Start Date : "+ $F{startDate})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="287" y="190" width="260" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("End Date : "+ $F{endDate})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="5" y="242" width="260" height="20" uuid="23a0a88a-df70-4d46-8a0e-083648c89dca"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Email :"+ $F{email})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="270" width="260" height="20" uuid="c4fc4cc3-8ddd-4007-ba0d-75a0f9f214e2"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Phone : "+ $F{phone})]]></textFieldExpression>
			</textField>
		</band>
	</title>
	<detail>
		<band height="304">
			<rectangle>
				<reportElement x="-3" y="10" width="558" height="150" forecolor="#4CB319" uuid="800e0b0b-0c92-4dc7-a7f3-d6d41a6bb980"/>
			</rectangle>
			<textField>
				<reportElement x="230" y="24" width="314" height="20" uuid="69b91147-cd37-4ed3-8e21-681fb6749878"/>
				<textElement textAlignment="Right">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("No of Customer Onboarded : "+ $F{description})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="230" y="59" width="314" height="20" uuid="69b91147-cd37-4ed3-8e21-681fb6749878"/>
				<textElement textAlignment="Right">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Total Commission : Rs."+ $F{charge})]]></textFieldExpression>
			</textField>
			<rectangle>
				<reportElement x="-2" y="185" width="558" height="30" forecolor="#4CB319" uuid="e2edfe46-eab5-4ad1-a44d-d0e1db40f821"/>
			</rectangle>
			<textField>
				<reportElement x="5" y="190" width="542" height="20" uuid="6891aaba-41ca-49f1-bcc8-1cdc357ce21a"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Total Payable in Words : "+ $F{totalAmountInWords})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="230" y="120" width="315" height="20" uuid="0ae15fe1-785b-433a-9ace-ef2b64656d38"/>
				<textElement textAlignment="Right">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Total Amount Payable : Rs."+ $F{totalDue})]]></textFieldExpression>
			</textField>
			<rectangle>
				<reportElement x="-2" y="10" width="222" height="150" forecolor="#4CB319" uuid="b628846b-5f4f-4080-ab2e-3ae855323621"/>
			</rectangle>
			<staticText>
				<reportElement x="6" y="24" width="205" height="20" uuid="a53bf401-665a-47fd-ad7c-441c40577ae9"/>
				<textElement textAlignment="Center">
					<font size="13" isBold="true"/>
				</textElement>
				<text><![CDATA[Commission Detail]]></text>
			</staticText>
			<textField>
				<reportElement x="4" y="59" width="205" height="20" uuid="d820a682-4cc7-4db7-900e-8b7633e8e50d"/>
				<textElement textAlignment="Center">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Type : "+ $F{planname})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="5" y="92" width="205" height="20" uuid="df2ed508-9144-4c71-827e-a34f5c04f2c4"/>
				<textElement textAlignment="Center">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Commission Value : Rs."+ $F{displayname})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="230" y="90" width="314" height="20" uuid="69b91147-cd37-4ed3-8e21-681fb6749878"/>
				<textElement textAlignment="Right">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Total Tax : Rs."+ $F{tax})]]></textFieldExpression>
			</textField>
		</band>
	</detail>
	<pageFooter>
		<band height="30">
			<textField>
				<reportElement mode="Opaque" x="0" y="0" width="515" height="13" backcolor="#E6E6E6" uuid="b8f7a38f-7c55-4e00-8050-80904948a04a"/>
				<textElement textAlignment="Right"/>
				<textFieldExpression><![CDATA["Page "+$V{PAGE_NUMBER}+" of"]]></textFieldExpression>
			</textField>
			<textField evaluationTime="Report">
				<reportElement mode="Opaque" x="515" y="0" width="40" height="13" backcolor="#E6E6E6" uuid="2114186e-67d4-4f5b-bd1a-23b37d669c29"/>
				<textFieldExpression><![CDATA[" " + $V{PAGE_NUMBER}]]></textFieldExpression>
			</textField>
		</band>
	</pageFooter>
</jasperReport>
' where templatetype='Comission';


insert into tbltemplatemanagement(templatename,templatetype,status) values
('PaymentReceipt','Payment','A');


update tbltemplatemanagement set jrxmlfile=
'
<?xml version="1.0" encoding="UTF-8"?>
<!-- Created with Jaspersoft Studio version 6.16.0.final using JasperReports Library version 6.16.0-48579d909b7943b64690c65c71e07e0b80981928  -->
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd" name="books" pageWidth="595" pageHeight="842" whenNoDataType="AllSectionsNoDetail" columnWidth="535" leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20" uuid="a162276b-35be-405f-b3ff-18f7d8feadb7">
	<property name="template.engine" value="tabular_template"/>
	<property name="com.jaspersoft.studio.data.defaultdataadapter" value="DataAdapter.xml"/>
	<property name="ireport.zoom" value="1.0"/>
	<property name="ireport.x" value="58"/>
	<property name="ireport.y" value="174"/>
	<style name="Table_TH" mode="Opaque" backcolor="#066990">
		<box>
			<topPen lineWidth="0.5" lineColor="#000000"/>
			<bottomPen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<style name="Table_CH" mode="Opaque" forecolor="#FFFFFF" backcolor="#06618F" hTextAlign="Center" fontSize="12">
		<box>
			<topPen lineWidth="0.5" lineColor="#000000"/>
			<bottomPen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<style name="Table_TD" mode="Opaque" backcolor="#FFFFFF" hTextAlign="Center">
		<box>
			<topPen lineWidth="0.5" lineColor="#000000"/>
			<bottomPen lineWidth="0.5" lineColor="#000000"/>
		</box>
	</style>
	<subDataset name="ChargeList" uuid="38b6f16c-4460-4ddd-b4e5-173b76dd6fa8">
		<field name="name" class="java.lang.String"/>
		<field name="price" class="java.lang.Double"/>
		<field name="tax" class="java.lang.Double"/>
		<field name="total" class="java.lang.Double"/>
		<field name="discount" class="java.lang.Double"/>
		<field name="comm_type" class="java.lang.String"/>
	</subDataset>
	<queryString language="XPath">
		<![CDATA[/invoice]]>
	</queryString>
	<field name="id" class="java.lang.String">
		<fieldDescription><![CDATA[id]]></fieldDescription>
	</field>
	<field name="number" class="java.lang.String">
		<fieldDescription><![CDATA[number]]></fieldDescription>
	</field>
	<field name="customerId" class="java.lang.String">
		<fieldDescription><![CDATA[customerId]]></fieldDescription>
	</field>
	<field name="billDate" class="java.lang.String">
		<fieldDescription><![CDATA[billDate]]></fieldDescription>
	</field>
	<field name="createDate" class="java.lang.String">
		<fieldDescription><![CDATA[createDate]]></fieldDescription>
	</field>
	<field name="startDate" class="java.lang.String">
		<fieldDescription><![CDATA[startDate]]></fieldDescription>
	</field>
	<field name="endDate" class="java.lang.String">
		<fieldDescription><![CDATA[endDate]]></fieldDescription>
	</field>
	<field name="dueDate" class="java.lang.String">
		<fieldDescription><![CDATA[dueDate]]></fieldDescription>
	</field>
	<field name="latePaymentDate" class="java.lang.String">
		<fieldDescription><![CDATA[latePaymentDate]]></fieldDescription>
	</field>
	<field name="charge" class="java.lang.String">
		<fieldDescription><![CDATA[charge]]></fieldDescription>
	</field>
	<field name="tax" class="java.lang.String">
		<fieldDescription><![CDATA[tax]]></fieldDescription>
	</field>
	<field name="discount" class="java.lang.String">
		<fieldDescription><![CDATA[discount]]></fieldDescription>
	</field>
	<field name="usage" class="java.lang.String">
		<fieldDescription><![CDATA[usage]]></fieldDescription>
	</field>
	<field name="payment" class="java.lang.String">
		<fieldDescription><![CDATA[payment]]></fieldDescription>
	</field>
	<field name="previousBalance" class="java.lang.String">
		<fieldDescription><![CDATA[previousBalance]]></fieldDescription>
	</field>
	<field name="latePaymentFee" class="java.lang.String">
		<fieldDescription><![CDATA[latePaymentFee]]></fieldDescription>
	</field>
	<field name="currentPayment" class="java.lang.String">
		<fieldDescription><![CDATA[currentPayment]]></fieldDescription>
	</field>
	<field name="currentDebit" class="java.lang.String">
		<fieldDescription><![CDATA[currentDebit]]></fieldDescription>
	</field>
	<field name="currentCredit" class="java.lang.String">
		<fieldDescription><![CDATA[currentCredit]]></fieldDescription>
	</field>
	<field name="totalDue" class="java.lang.String">
		<fieldDescription><![CDATA[totalDue]]></fieldDescription>
	</field>
	<field name="totalAmountInWords" class="java.lang.String">
		<fieldDescription><![CDATA[totalAmountInWords]]></fieldDescription>
	</field>
	<field name="totalDueInWords" class="java.lang.String">
		<fieldDescription><![CDATA[totalDueInWords]]></fieldDescription>
	</field>
	<field name="accountnumber" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/accountnumber]]></fieldDescription>
	</field>
	<field name="email" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/email]]></fieldDescription>
	</field>
	<field name="firstname" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/firstname]]></fieldDescription>
	</field>
	<field name="lastname" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/lastname]]></fieldDescription>
	</field>
	<field name="name" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/name]]></fieldDescription>
	</field>
	<field name="phone" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/phone]]></fieldDescription>
	</field>
	<field name="subscriberpackage" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/subscriberpackage]]></fieldDescription>
	</field>
	<field name="subscriberpackageid" class="java.lang.String">
		<fieldDescription><![CDATA[customerInformation/subscriberpackageid]]></fieldDescription>
	</field>
	<field name="address1" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/address1]]></fieldDescription>
	</field>
	<field name="address2" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/address2]]></fieldDescription>
	</field>
	<field name="addresstype" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/addresstype]]></fieldDescription>
	</field>
	<field name="city" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/city]]></fieldDescription>
	</field>
	<field name="country" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/country]]></fieldDescription>
	</field>
	<field name="pincode" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/pincode]]></fieldDescription>
	</field>
	<field name="state" class="java.lang.String">
		<fieldDescription><![CDATA[addressDetail/state]]></fieldDescription>
	</field>
	<field name="alias" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/alias]]></fieldDescription>
	</field>
	<field name="description" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/description]]></fieldDescription>
	</field>
	<field name="downloadqos" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/downloadqos]]></fieldDescription>
	</field>
	<field name="enddate" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/enddate]]></fieldDescription>
	</field>
	<field name="planname" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/name]]></fieldDescription>
	</field>
	<field name="displayname" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/displayname]]></fieldDescription>
	</field>
	<field name="postpaidplanid" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/postpaidplanid]]></fieldDescription>
	</field>
	<field name="quota" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/quota]]></fieldDescription>
	</field>
	<field name="quotaunit" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/quotaunit]]></fieldDescription>
	</field>
	<field name="status" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/status]]></fieldDescription>
	</field>
	<field name="uploadqos" class="java.lang.String">
		<fieldDescription><![CDATA[planInformation/uploadqos]]></fieldDescription>
	</field>
	<field name="laststatuschange" class="java.lang.String">
		<fieldDescription><![CDATA[laststatuschange]]></fieldDescription>
	</field>
	<field name="invoicelist" class="java.lang.String"/>
	<title>
		<band height="390">
			<frame>
				<reportElement mode="Opaque" x="-20" y="-20" width="595" height="50" backcolor="#4CB319" uuid="333d5d27-232f-4002-931f-2ea09a376000"/>
				<image scaleImage="RetainShape">
					<reportElement x="20" y="10" width="555" height="30" forecolor="#FFFFFF" uuid="9b6cb13d-a79b-4035-9a8c-4e1015eb823a"/>
					<imageExpression><![CDATA["C://logo_new.png"]]></imageExpression>
				</image>
				<image scaleImage="RetainShape">
					<reportElement x="520" y="10" width="59" height="30" forecolor="#FFFFFF" uuid="9b6cb13d-a79b-4035-9a8c-4e1015eb823a"/>
					<imageExpression><![CDATA["C://logo_new.png"]]></imageExpression>
				</image>
				<staticText>
					<reportElement x="180" y="10" width="220" height="30" forecolor="#FFFFFF" uuid="0658bd99-4ef5-4f51-b195-cdee9eb72334"/>
					<textElement textAlignment="Center">
						<font size="16" isBold="true" isUnderline="true"/>
					</textElement>
					<text><![CDATA[Payment Receipt]]></text>
				</staticText>
			</frame>
			<rectangle>
				<reportElement x="-3" y="43" width="558" height="347" forecolor="#4CB319" uuid="43a8d4cf-6f4b-4411-8513-d359e1542065"/>
			</rectangle>
			<textField>
				<reportElement x="3" y="114" width="527" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Address : "+ $F{address1})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="150" width="527" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[$F{address2}]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="6" y="180" width="524" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("City : "+ $F{city})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="6" y="210" width="524" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("State : "+ $F{state})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="6" y="240" width="524" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Country : "+ $F{country})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="6" y="270" width="524" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Pincode : "+ $F{pincode})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="6" y="50" width="524" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Receipt No # "+ $F{number})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="6" y="360" width="524" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Partner Id : "+ $F{customerId})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="3" y="80" width="527" height="20" uuid="8da6214e-dad2-4cb7-bedd-5e8adfaf0ddd"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Receipt Date : "+ $F{billDate})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="6" y="300" width="524" height="20" uuid="23a0a88a-df70-4d46-8a0e-083648c89dca"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Email :"+ $F{email})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="6" y="330" width="524" height="20" uuid="c4fc4cc3-8ddd-4007-ba0d-75a0f9f214e2"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Phone : "+ $F{phone})]]></textFieldExpression>
			</textField>
		</band>
	</title>
	<detail>
		<band height="304">
			<rectangle>
				<reportElement x="-3" y="10" width="558" height="150" forecolor="#4CB319" uuid="800e0b0b-0c92-4dc7-a7f3-d6d41a6bb980"/>
			</rectangle>
			<textField>
				<reportElement x="7" y="107" width="542" height="20" uuid="6891aaba-41ca-49f1-bcc8-1cdc357ce21a"/>
				<textElement textAlignment="Left">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Total Amount Paid In Words : "+ $F{totalAmountInWords})]]></textFieldExpression>
			</textField>
			<textField>
				<reportElement x="11" y="50" width="530" height="20" uuid="0ae15fe1-785b-433a-9ace-ef2b64656d38"/>
				<textElement textAlignment="Right">
					<font size="13" isBold="true"/>
				</textElement>
				<textFieldExpression><![CDATA[new String("Total Amount Paid : Rs."+ $F{payment})]]></textFieldExpression>
			</textField>
			<staticText>
				<reportElement x="180" y="20" width="205" height="20" uuid="a53bf401-665a-47fd-ad7c-441c40577ae9"/>
				<textElement textAlignment="Center">
					<font size="13" isBold="true"/>
				</textElement>
				<text><![CDATA[Payment Detail]]></text>
			</staticText>
		</band>
	</detail>
	<pageFooter>
		<band height="30">
			<textField>
				<reportElement mode="Opaque" x="0" y="0" width="515" height="13" backcolor="#E6E6E6" uuid="b8f7a38f-7c55-4e00-8050-80904948a04a"/>
				<textElement textAlignment="Right"/>
				<textFieldExpression><![CDATA["Page "+$V{PAGE_NUMBER}+" of"]]></textFieldExpression>
			</textField>
			<textField evaluationTime="Report">
				<reportElement mode="Opaque" x="515" y="0" width="40" height="13" backcolor="#E6E6E6" uuid="2114186e-67d4-4f5b-bd1a-23b37d669c29"/>
				<textFieldExpression><![CDATA[" " + $V{PAGE_NUMBER}]]></textFieldExpression>
			</textField>
		</band>
	</pageFooter>
</jasperReport>
' where templatetype='Payment';



