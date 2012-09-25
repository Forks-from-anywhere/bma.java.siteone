/**
 * Autogenerated by Thrift Compiler (0.8.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package bma.siteone.cloud.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCloudResponse implements org.apache.thrift.TBase<TCloudResponse, TCloudResponse._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TCloudResponse");

  private static final org.apache.thrift.protocol.TField TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("type", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField CONTENT_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("contentType", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField CONTENT_FIELD_DESC = new org.apache.thrift.protocol.TField("content", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField LOGTRACK_FIELD_DESC = new org.apache.thrift.protocol.TField("logtrack", org.apache.thrift.protocol.TType.LIST, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TCloudResponseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TCloudResponseTupleSchemeFactory());
  }

  public int type; // required
  public String contentType; // required
  public ByteBuffer content; // required
  public List<String> logtrack; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    TYPE((short)1, "type"),
    CONTENT_TYPE((short)2, "contentType"),
    CONTENT((short)3, "content"),
    LOGTRACK((short)4, "logtrack");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // TYPE
          return TYPE;
        case 2: // CONTENT_TYPE
          return CONTENT_TYPE;
        case 3: // CONTENT
          return CONTENT;
        case 4: // LOGTRACK
          return LOGTRACK;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __TYPE_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.TYPE, new org.apache.thrift.meta_data.FieldMetaData("type", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.CONTENT_TYPE, new org.apache.thrift.meta_data.FieldMetaData("contentType", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CONTENT, new org.apache.thrift.meta_data.FieldMetaData("content", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.LOGTRACK, new org.apache.thrift.meta_data.FieldMetaData("logtrack", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TCloudResponse.class, metaDataMap);
  }

  public TCloudResponse() {
  }

  public TCloudResponse(
    int type,
    String contentType,
    ByteBuffer content,
    List<String> logtrack)
  {
    this();
    this.type = type;
    setTypeIsSet(true);
    this.contentType = contentType;
    this.content = content;
    this.logtrack = logtrack;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TCloudResponse(TCloudResponse other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.type = other.type;
    if (other.isSetContentType()) {
      this.contentType = other.contentType;
    }
    if (other.isSetContent()) {
      this.content = org.apache.thrift.TBaseHelper.copyBinary(other.content);
;
    }
    if (other.isSetLogtrack()) {
      List<String> __this__logtrack = new ArrayList<String>();
      for (String other_element : other.logtrack) {
        __this__logtrack.add(other_element);
      }
      this.logtrack = __this__logtrack;
    }
  }

  public TCloudResponse deepCopy() {
    return new TCloudResponse(this);
  }

  @Override
  public void clear() {
    setTypeIsSet(false);
    this.type = 0;
    this.contentType = null;
    this.content = null;
    this.logtrack = null;
  }

  public int getType() {
    return this.type;
  }

  public TCloudResponse setType(int type) {
    this.type = type;
    setTypeIsSet(true);
    return this;
  }

  public void unsetType() {
    __isset_bit_vector.clear(__TYPE_ISSET_ID);
  }

  /** Returns true if field type is set (has been assigned a value) and false otherwise */
  public boolean isSetType() {
    return __isset_bit_vector.get(__TYPE_ISSET_ID);
  }

  public void setTypeIsSet(boolean value) {
    __isset_bit_vector.set(__TYPE_ISSET_ID, value);
  }

  public String getContentType() {
    return this.contentType;
  }

  public TCloudResponse setContentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  public void unsetContentType() {
    this.contentType = null;
  }

  /** Returns true if field contentType is set (has been assigned a value) and false otherwise */
  public boolean isSetContentType() {
    return this.contentType != null;
  }

  public void setContentTypeIsSet(boolean value) {
    if (!value) {
      this.contentType = null;
    }
  }

  public byte[] getContent() {
    setContent(org.apache.thrift.TBaseHelper.rightSize(content));
    return content == null ? null : content.array();
  }

  public ByteBuffer bufferForContent() {
    return content;
  }

  public TCloudResponse setContent(byte[] content) {
    setContent(content == null ? (ByteBuffer)null : ByteBuffer.wrap(content));
    return this;
  }

  public TCloudResponse setContent(ByteBuffer content) {
    this.content = content;
    return this;
  }

  public void unsetContent() {
    this.content = null;
  }

  /** Returns true if field content is set (has been assigned a value) and false otherwise */
  public boolean isSetContent() {
    return this.content != null;
  }

  public void setContentIsSet(boolean value) {
    if (!value) {
      this.content = null;
    }
  }

  public int getLogtrackSize() {
    return (this.logtrack == null) ? 0 : this.logtrack.size();
  }

  public java.util.Iterator<String> getLogtrackIterator() {
    return (this.logtrack == null) ? null : this.logtrack.iterator();
  }

  public void addToLogtrack(String elem) {
    if (this.logtrack == null) {
      this.logtrack = new ArrayList<String>();
    }
    this.logtrack.add(elem);
  }

  public List<String> getLogtrack() {
    return this.logtrack;
  }

  public TCloudResponse setLogtrack(List<String> logtrack) {
    this.logtrack = logtrack;
    return this;
  }

  public void unsetLogtrack() {
    this.logtrack = null;
  }

  /** Returns true if field logtrack is set (has been assigned a value) and false otherwise */
  public boolean isSetLogtrack() {
    return this.logtrack != null;
  }

  public void setLogtrackIsSet(boolean value) {
    if (!value) {
      this.logtrack = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case TYPE:
      if (value == null) {
        unsetType();
      } else {
        setType((Integer)value);
      }
      break;

    case CONTENT_TYPE:
      if (value == null) {
        unsetContentType();
      } else {
        setContentType((String)value);
      }
      break;

    case CONTENT:
      if (value == null) {
        unsetContent();
      } else {
        setContent((ByteBuffer)value);
      }
      break;

    case LOGTRACK:
      if (value == null) {
        unsetLogtrack();
      } else {
        setLogtrack((List<String>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case TYPE:
      return Integer.valueOf(getType());

    case CONTENT_TYPE:
      return getContentType();

    case CONTENT:
      return getContent();

    case LOGTRACK:
      return getLogtrack();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case TYPE:
      return isSetType();
    case CONTENT_TYPE:
      return isSetContentType();
    case CONTENT:
      return isSetContent();
    case LOGTRACK:
      return isSetLogtrack();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TCloudResponse)
      return this.equals((TCloudResponse)that);
    return false;
  }

  public boolean equals(TCloudResponse that) {
    if (that == null)
      return false;

    boolean this_present_type = true;
    boolean that_present_type = true;
    if (this_present_type || that_present_type) {
      if (!(this_present_type && that_present_type))
        return false;
      if (this.type != that.type)
        return false;
    }

    boolean this_present_contentType = true && this.isSetContentType();
    boolean that_present_contentType = true && that.isSetContentType();
    if (this_present_contentType || that_present_contentType) {
      if (!(this_present_contentType && that_present_contentType))
        return false;
      if (!this.contentType.equals(that.contentType))
        return false;
    }

    boolean this_present_content = true && this.isSetContent();
    boolean that_present_content = true && that.isSetContent();
    if (this_present_content || that_present_content) {
      if (!(this_present_content && that_present_content))
        return false;
      if (!this.content.equals(that.content))
        return false;
    }

    boolean this_present_logtrack = true && this.isSetLogtrack();
    boolean that_present_logtrack = true && that.isSetLogtrack();
    if (this_present_logtrack || that_present_logtrack) {
      if (!(this_present_logtrack && that_present_logtrack))
        return false;
      if (!this.logtrack.equals(that.logtrack))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(TCloudResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    TCloudResponse typedOther = (TCloudResponse)other;

    lastComparison = Boolean.valueOf(isSetType()).compareTo(typedOther.isSetType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type, typedOther.type);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetContentType()).compareTo(typedOther.isSetContentType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetContentType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.contentType, typedOther.contentType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetContent()).compareTo(typedOther.isSetContent());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetContent()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.content, typedOther.content);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLogtrack()).compareTo(typedOther.isSetLogtrack());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLogtrack()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.logtrack, typedOther.logtrack);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TCloudResponse(");
    boolean first = true;

    sb.append("type:");
    sb.append(this.type);
    first = false;
    if (!first) sb.append(", ");
    sb.append("contentType:");
    if (this.contentType == null) {
      sb.append("null");
    } else {
      sb.append(this.contentType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("content:");
    if (this.content == null) {
      sb.append("null");
    } else {
      org.apache.thrift.TBaseHelper.toString(this.content, sb);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("logtrack:");
    if (this.logtrack == null) {
      sb.append("null");
    } else {
      sb.append(this.logtrack);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bit_vector = new BitSet(1);
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TCloudResponseStandardSchemeFactory implements SchemeFactory {
    public TCloudResponseStandardScheme getScheme() {
      return new TCloudResponseStandardScheme();
    }
  }

  private static class TCloudResponseStandardScheme extends StandardScheme<TCloudResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TCloudResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.type = iprot.readI32();
              struct.setTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // CONTENT_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.contentType = iprot.readString();
              struct.setContentTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // CONTENT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.content = iprot.readBinary();
              struct.setContentIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // LOGTRACK
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list10 = iprot.readListBegin();
                struct.logtrack = new ArrayList<String>(_list10.size);
                for (int _i11 = 0; _i11 < _list10.size; ++_i11)
                {
                  String _elem12; // required
                  _elem12 = iprot.readString();
                  struct.logtrack.add(_elem12);
                }
                iprot.readListEnd();
              }
              struct.setLogtrackIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TCloudResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(TYPE_FIELD_DESC);
      oprot.writeI32(struct.type);
      oprot.writeFieldEnd();
      if (struct.contentType != null) {
        oprot.writeFieldBegin(CONTENT_TYPE_FIELD_DESC);
        oprot.writeString(struct.contentType);
        oprot.writeFieldEnd();
      }
      if (struct.content != null) {
        oprot.writeFieldBegin(CONTENT_FIELD_DESC);
        oprot.writeBinary(struct.content);
        oprot.writeFieldEnd();
      }
      if (struct.logtrack != null) {
        oprot.writeFieldBegin(LOGTRACK_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.logtrack.size()));
          for (String _iter13 : struct.logtrack)
          {
            oprot.writeString(_iter13);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TCloudResponseTupleSchemeFactory implements SchemeFactory {
    public TCloudResponseTupleScheme getScheme() {
      return new TCloudResponseTupleScheme();
    }
  }

  private static class TCloudResponseTupleScheme extends TupleScheme<TCloudResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TCloudResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetType()) {
        optionals.set(0);
      }
      if (struct.isSetContentType()) {
        optionals.set(1);
      }
      if (struct.isSetContent()) {
        optionals.set(2);
      }
      if (struct.isSetLogtrack()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetType()) {
        oprot.writeI32(struct.type);
      }
      if (struct.isSetContentType()) {
        oprot.writeString(struct.contentType);
      }
      if (struct.isSetContent()) {
        oprot.writeBinary(struct.content);
      }
      if (struct.isSetLogtrack()) {
        {
          oprot.writeI32(struct.logtrack.size());
          for (String _iter14 : struct.logtrack)
          {
            oprot.writeString(_iter14);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TCloudResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.type = iprot.readI32();
        struct.setTypeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.contentType = iprot.readString();
        struct.setContentTypeIsSet(true);
      }
      if (incoming.get(2)) {
        struct.content = iprot.readBinary();
        struct.setContentIsSet(true);
      }
      if (incoming.get(3)) {
        {
          org.apache.thrift.protocol.TList _list15 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.logtrack = new ArrayList<String>(_list15.size);
          for (int _i16 = 0; _i16 < _list15.size; ++_i16)
          {
            String _elem17; // required
            _elem17 = iprot.readString();
            struct.logtrack.add(_elem17);
          }
        }
        struct.setLogtrackIsSet(true);
      }
    }
  }

}

