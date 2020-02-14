/*
 * Copyright 2020 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2020 Ping Identity Corporation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.unboundid.ldap.sdk.unboundidds.extensions;



import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.IntermediateResponse;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.NotMutable;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;

import static com.unboundid.ldap.sdk.unboundidds.extensions.ExtOpMessages.*;



/**
 * This class provides an implementation of an intermediate response that can
 * provide the client with a portion of the support data archive generated in
 * response to a {@link CollectSupportDataExtendedRequest}.
 * <BR>
 * <BLOCKQUOTE>
 *   <B>NOTE:</B>  This class, and other classes within the
 *   {@code com.unboundid.ldap.sdk.unboundidds} package structure, are only
 *   supported for use against Ping Identity, UnboundID, and
 *   Nokia/Alcatel-Lucent 8661 server products.  These classes provide support
 *   for proprietary functionality or for external specifications that are not
 *   considered stable or mature enough to be guaranteed to work in an
 *   interoperable way with other types of LDAP servers.
 * </BLOCKQUOTE>
 * <BR>
 * The collect support data archive fragment intermediate response has an OID of
 * 1.3.6.1.4.1.30221.2.6.66 and a value with the following encoding:
 * <BR>
 * <PRE>
 *   CollectSupportDataArchiveDataIntermediateResponse ::= SEQUENCE {
 *      totalArchiveSizeBytes     [0] INTEGER,
 *      moreDataToReturn          [1] BOOLEAN,
 *      fragmentData              [2] OCTET STRING,
 *      ... }
 * </PRE>
 *
 * @see  CollectSupportDataExtendedRequest
 * @see  CollectSupportDataExtendedResult
 * @see  CollectSupportDataOutputIntermediateResponse
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CollectSupportDataArchiveFragmentIntermediateResponse
       extends IntermediateResponse
{
  /**
   * The OID (1.3.6.1.4.1.30221.2.6.66) for the collect support data archive
   * fragment intermediate response.
   */
  public static final String
       COLLECT_SUPPORT_DATA_ARCHIVE_FRAGMENT_INTERMEDIATE_RESPONSE_OID =
       "1.3.6.1.4.1.30221.2.6.66";



  /**
   * The BER type for the value element that holds the total size of the
   * support data archive, in bytes.
   */
  private static final byte TYPE_TOTAL_ARCHIVE_SIZE_BYTES = (byte) 0x80;



  /**
   * The BER type for the value element that indicates whether there is still
   * more of the support data archive to be returned.
   */
  private static final byte TYPE_MORE_DATA_TO_RETURN = (byte) 0x81;



  /**
   * The BER type for the value element that holds the data for this fragment of
   * the support data archive.
   */
  private static final byte TYPE_FRAGMENT_DATA = (byte) 0x82;


  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 6989352662115346422L;



  // Indicates whether there is still more of the support data archive to be
  // returned to the client.
  private final boolean moreDataToReturn;

  // The data that comprises this fragment of the support data archive.
  private final byte[] fragmentData;

  // The total size of the support data archive, in bytes.
  private final long totalArchiveSizeBytes;



  /**
   * Creates a new collect support data archive fragment intermediate response
   * object with the provided information.
   *
   * @param  totalArchiveSizeBytes  The size, in bytes, of the complete
   *                                support data archive.
   * @param  moreDataToReturn       Indicates whether there are more fragments
   *                                to be returned to as part of the complete
   *                                support data archive.
   * @param  fragmentData           The data contained in this fragment of the
   *                                support data archive.  It must not be
   *                                {@code null}.
   * @param  controls               The set of controls to include in this
   *                                intermediate response.  It may be
   *                                {@code null} or empty if no controls should
   *                                be included.
   */
  public CollectSupportDataArchiveFragmentIntermediateResponse(
              final long totalArchiveSizeBytes, final boolean moreDataToReturn,
              final byte[] fragmentData, final Control... controls)
  {
    super(COLLECT_SUPPORT_DATA_ARCHIVE_FRAGMENT_INTERMEDIATE_RESPONSE_OID,
         encodeValue(totalArchiveSizeBytes, moreDataToReturn, fragmentData),
         controls);

    this.totalArchiveSizeBytes = totalArchiveSizeBytes;
    this.moreDataToReturn = moreDataToReturn;
    this.fragmentData = fragmentData;
  }



  /**
   * Constructs an ASN.1 octet string suitable for use as the value of this
   * collect support data archive fragment intermediate response.
   *
   * @param  totalArchiveSizeBytes  The size, in bytes, of the complete
   *                                support data archive.
   * @param  moreDataToReturn       Indicates whether there are more fragments
   *                                to be returned to as part of the complete
   *                                support data archive.
   * @param  fragmentData           The data contained in this fragment of the
   *                                support data archive.  It must not be
   *                                {@code null}.
   *
   * @return  The ASN.1 octet string containing the encoded value.
   */
  private static ASN1OctetString encodeValue(final long totalArchiveSizeBytes,
                                             final boolean moreDataToReturn,
                                             final byte[] fragmentData)
  {
    final ASN1Sequence valueSequence = new ASN1Sequence(
         new ASN1Long(TYPE_TOTAL_ARCHIVE_SIZE_BYTES, totalArchiveSizeBytes),
         new ASN1Boolean(TYPE_MORE_DATA_TO_RETURN, moreDataToReturn),
         new ASN1OctetString(TYPE_FRAGMENT_DATA, fragmentData));
    return new ASN1OctetString(valueSequence.encode());
  }



  /**
   * Creates a new collect support data archive fragment intermediate response
   * that is decoded from the provided generic intermediate response.
   *
   * @param  intermediateResponse  The generic intermediate response to be
   *                               decoded as a collect support data archive
   *                               fragment intermediate response.  It must not
   *                               be {@code null}.
   *
   * @throws  LDAPException  If the provided intermediate response object cannot
   *                         be decoded as a collect support data archive
   *                         fragment intermediate response.
   */
  public CollectSupportDataArchiveFragmentIntermediateResponse(
              final IntermediateResponse intermediateResponse)
         throws LDAPException
  {
    super(intermediateResponse);

    final ASN1OctetString value = intermediateResponse.getValue();
    if (value == null)
    {
      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_CSD_FRAGMENT_IR_DECODE_NO_VALUE.get());
    }

    try
    {
      final ASN1Sequence valueSequence =
           ASN1Sequence.decodeAsSequence(value.getValue());
      final ASN1Element[] elements = valueSequence.elements();
      totalArchiveSizeBytes = ASN1Long.decodeAsLong(elements[0]).longValue();
      moreDataToReturn =
           ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue();
      fragmentData =
           ASN1OctetString.decodeAsOctetString(elements[2]).getValue();
    }
    catch (final Exception e)
    {
      Debug.debugException(e);
      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_CSD_FRAGMENT_IR_DECODE_ERROR.get(
                StaticUtils.getExceptionMessage(e)),
           e);
    }
  }



  /**
   * Retrieves the total number of bytes contained in the complete support data
   * archive.
   *
   * @return  The total number of bytes contained in the complete support data
   *          archive.
   */
  public long getTotalArchiveSizeBytes()
  {
    return totalArchiveSizeBytes;
  }



  /**
   * Indicates whether there are one or more fragments still to be returned
   * in the complete support data archive.
   *
   * @return  {@code true} if there are still more fragments to be returned, or
   *          {@code false} if not.
   */
  public boolean moreDataToReturn()
  {
    return moreDataToReturn;
  }



  /**
   * Retrieves the data included in this fragment.
   *
   * @return  The data included in this fragment.
   */
  public byte[] getFragmentData()
  {
    return fragmentData;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getIntermediateResponseName()
  {
    return INFO_COLLECT_SUPPORT_DATA_FRAGMENT_IR_NAME.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String valueToString()
  {
    final StringBuilder buffer = new StringBuilder();

    buffer.append("totalArchiveSizeBytes=");
    buffer.append(totalArchiveSizeBytes);
    buffer.append(" moreDataToReturn=");
    buffer.append(moreDataToReturn);
    buffer.append(" fragmentSizeBytes=");
    buffer.append(fragmentData.length);

    return buffer.toString();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append(
         "CollectSupportDataArchiveFragmentIntermediateResponse(oid='");
    buffer.append(getOID());
    buffer.append("', totalArchiveSizeBytes=");
    buffer.append(totalArchiveSizeBytes);
    buffer.append(", moreDataToReturn=");
    buffer.append(moreDataToReturn);
    buffer.append(", fragmentSizeBytes=");
    buffer.append(fragmentData.length);

    final Control[] controls = getControls();
    if (controls.length > 0)
    {
      buffer.append(", controls={");
      for (int i=0; i < controls.length; i++)
      {
        if (i > 0)
        {
          buffer.append(", ");
        }

        buffer.append(controls[i]);
      }
      buffer.append('}');
    }

    buffer.append(')');
  }
}
