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

public class TCloudRequest implements org.apache.thrift.TBase<TCloudRequest, TCloudRequest._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TCloudRequest");

  private static final org.apache.thrift.protocol.TField ENTRY_FIELD_DESC = new org.apache.thrift.protocol.TField("entry", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField CONTENT_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("contentType", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField CONTENT_FIELD_DESC = new org.apache.thrift.protocol.TField("content", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField CONTEXT_FIELD_DESC = new org.apache.thrift.protocol.TField("context", org.apache.thrift.protocol.TType.MAP, (short)4);
  private static final org.apache.thrift.protocol.TField CALLBACK_FIELD_DESC = new org.apache.thrift.protocol.TField("callback", org.apache.thrift.protocol.TType.STRUCT, (short)5);
  private static final org.apache.thrift.protocol.TField LOGTRACK_FIELD_DESC = new org.apache.thrift.protocol.TField("logtrack", org.apache.thrift.protocol.TType.BOOL, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TCloudRequestStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TCloudRequestTupleSchemeFactory());
  }

  public TCloudEntry entry; // required
  public String contentType; // required
  public ByteBuffer content; // required
  public Map<String,String> context; // required
  public TCloudEntry callback; // required
  public boolean logtrack; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ENTRY((short)1, "entry"),
    CONTENT_TYPE((short)2, "contentType"),
    CONTENT((short)3, "content"),
    CONTEXT((short)4, "context"),
    CALLBACK((short)5, "callback"),
    LOGTRACK((short)6, "logtrack");

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
        case 1: // ENTRY
          return ENTRY;
        case 2: // CONTENT_TYPE
          return CONTENT_TYPE;
        case 3: // CONTENT
          return CONTENT;
        case 4: // CONTEXT
          return CONTEXT;
        case 5: // CALLBACK
          return CALLBACK;
        case 6: // LOGTRACK
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
  private static final int __LOGTRACK_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ENTRY, new org.apache.thrift.meta_data.FieldMetaData("entry", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TCloudEntry.class)));
    tmpMap.put(_Fields.CONTENT_TYPE, new org.apache.thrift.meta_data.FieldMetaData("contentType", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CONTENT, new org.apache.thrift.meta_data.FieldMetaData("content", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.CONTEXT, new org.apache.thrift.meta_data.FieldMetaData("context", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.CALLBACK, new org.apache.thrift.meta_data.FieldMetaData("callback", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TCloudEntry.class)));
    tmpMap.put(_Fields.LOGTRACK, new org.apache.thrift.meta_data.FieldMetaData("logtrack", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TCloudRequest.class, metaDataMap);
  }

  public TCloudRequest() {
  }

  public TCloudRequest(
    TCloudEntry entry,
    String contentType,
    ByteBuffer content,
    Map<String,String> context,
    TCloudEntry callback,
    boolean logtrack)
  {
    this();
    this.entry = entry;
    this.contentType = contentType;
    this.content = content;
    this.context = context;
    this.callback = callback;
    this.logtrack = logtrack;
    setLogtrackIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TCloudRequest(TCloudRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetEntry()) {
      this.entry = new TCloudEntry(other.entry);
    }
    if (other.isSetContentType()) {
      this.contentType = other.contentType;
    }
    if (other.isSetContent()) {
      this.content = org.apache.thrift.TBaseHelper.copyBinary(other.content);
;
    }
    if (other.isSetContext()) {
      Map<String,String> __this__context = new HashMap<String,String>();
      for (Map.Entry<String, String> other_element : other.context.entrySet()) {

        String other_element_key = other_element.getKey();
        String other_element_value = other_element.getValue();

        String __this__context_copy_key = other_element_key;

        String __this__context_copy_value = other_element_value;

        __this__context.put(__this__context_copy_key, __this__context_copy_value);
      }
      this.context = __this__context;
    }
    if (other.isSetCallback()) {
      this.callback = new TCloudEntry(other.callback);
    }
    this.logtrack = other.logtrack;
  }

  public TCloudRequest deepCopy() {
    return new TCloudRequest(this);
  }

  @Override
  public void clear() {
    this.entry = null;
    this.contentType = null;
    this.content = null;
    this.context = null;
    this.callback = null;
    setLogtrackIsSet(false);
    this.logtrack = false;
  }

  public TCloudEntry getEntry() {
    return this.entry;
  }

  public TCloudRequest setEntry(TCloudEntry entry) {
    this.entry = entry;
    return this;
  }

  public void unsetEntry() {
    this.entry = null;
  }

  /** Returns true if field entry is set (has been assigned a value) and false otherwise */
  public boolean isSetEntry() {
    return this.entry != null;
  }

  public void setEntryIsSet(boolean value) {
    if (!value) {
      this.entry = null;
    }
  }

  public String getContentType() {
    return this.contentType;
  }

  public TCloudRequest setContentType(String contentType) {
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

  public TCloudRequest setContent(byte[] content) {
    setContent(content == null ? (ByteBuffer)null : ByteBuffer.wrap(content));
    return this;
  }

  public TCloudRequest setContent(ByteBuffer content) {
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

  public int getContextSize() {
    return (this.context == null) ? 0 : this.context.size();
  }

  public void putToContext(String key, String val) {
    if (this.context == null) {
      this.context = new HashMap<String,String>();
    }
    this.context.put(key, val);
  }

  public Map<String,String> getContext() {
    return this.context;
  }

  public TCloudRequest setContext(Map<String,String> context) {
    this.context = context;
    return this;
  }

  public void unsetContext() {
    this.context = null;
  }

  /** Returns true if field context is set (has been assigned a value) and false otherwise */
  public boolean isSetContext() {
    return this.context != null;
  }

  public void setContextIsSet(boolean value) {
    if (!value) {
      this.context = null;
    }
  }

  public TCloudEntry getCallback() {
    return this.callback;
  }

  public TCloudRequest setCallback(TCloudEntry callback) {
    this.callback = callback;
    return this;
  }

  public void unsetCallback() {
    this.callback = null;
  }

  /** Returns true if field callback is set (has been assigned a value) and false otherwise */
  public boolean isSetCallback() {
    return this.callback != null;
  }

  public void setCallbackIsSet(boolean value) {
    if (!value) {
      this.callback = null;
    }
  }

  public boolean isLogtrack() {
    return this.logtrack;
  }

  public TCloudRequest setLogtrack(boolean logtrack) {
    this.logtrack = logtrack;
    setLogtrackIsSet(true);
    return this;
  }

  public void unsetLogtrack() {
    __isset_bit_vector.clear(__LOGTRACK_ISSET_ID);
  }

  /** Returns true if field logtrack is set (has been assigned a value) and false otherwise */
  public boolean isSetLogtrack() {
    return __isset_bit_vector.get(__LOGTRACK_ISSET_ID);
  }

  public void setLogtrackIsSet(boolean value) {
    __isset_bit_vector.set(__LOGTRACK_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ENTRY:
      if (value == null) {
        unsetEntry();
      } else {
        setEntry((TCloudEntry)value);
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

    case CONTEXT:
      if (value == null) {
        unsetContext();
      } else {
        setContext((Map<String,String>)value);
      }
      break;

    case CALLBACK:
      if (value == null) {
        unsetCallback();
      } else {
        setCallback((TCloudEntry)value);
      }
      break;

    case LOGTRACK:
      if (value == null) {
        unsetLogtrack();
      } else {
        setLogtrack((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ENTRY:
      return getEntry();

    case CONTENT_TYPE:
      return getContentType();

    case CONTENT:
      return getContent();

    case CONTEXT:
      return getContext();

    case CALLBACK:
      return getCallback();

    case LOGTRACK:
      return Boolean.valueOf(isLogtrack());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ENTRY:
      return isSetEntry();
    case CONTENT_TYPE:
      return isSetContentType();
    case CONTENT:
      return isSetContent();
    case CONTEXT:
      return isSetContext();
    case CALLBACK:
      return isSetCallback();
    case LOGTRACK:
      return isSetLogtrack();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TCloudRequest)
      return this.equals((TCloudRequest)that);
    return false;
  }

  public boolean equals(TCloudRequest that) {
    if (that == null)
      return false;

    boolean this_present_entry = true && this.isSetEntry();
    boolean that_present_entry = true && that.isSetEntry();
    if (this_present_entry || that_present_entry) {
      if (!(this_present_entry && that_present_entry))
        return false;
      if (!this.entry.equals(that.entry))
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

    boolean this_present_context = true && this.isSetContext();
    boolean that_present_context = true && that.isSetContext();
    if (this_present_context || that_present_context) {
      if (!(this_present_context && that_present_context))
        return false;
      if (!this.context.equals(that.context))
        return false;
    }

    boolean this_present_callback = true && this.isSetCallback();
    boolean that_present_callback = true && that.isSetCallback();
    if (this_present_callback || that_present_callback) {
      if (!(this_present_callback && that_present_callback))
        return false;
      if (!this.callback.equals(that.callback))
        return false;
    }

    boolean this_present_logtrack = true;
    boolean that_present_logtrack = true;
    if (this_present_logtrack || that_present_logtrack) {
      if (!(this_present_logtrack && that_present_logtrack))
        return false;
      if (this.logtrack != that.logtrack)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(TCloudRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    TCloudRequest typedOther = (TCloudRequest)other;

    lastComparison = Boolean.valueOf(isSetEntry()).compareTo(typedOther.isSetEntry());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEntry()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.entry, typedOther.entry);
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
    lastComparison = Boolean.valueOf(isSetContext()).compareTo(typedOther.isSetContext());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetContext()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.context, typedOther.context);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCallback()).compareTo(typedOther.isSetCallback());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCallback()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.callback, typedOther.callback);
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
    StringBuilder sb = new StringBuilder("TCloudRequest(");
    boolean first = true;

    sb.append("entry:");
    if (this.entry == null) {
      sb.append("null");
    } else {
      sb.append(this.entry);
    }
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
    sb.append("context:");
    if (this.context == null) {
      sb.append("null");
    } else {
      sb.append(this.context);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("callback:");
    if (this.callback == null) {
      sb.append("null");
    } else {
      sb.append(this.callback);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("logtrack:");
    sb.append(this.logtrack);
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

  private static class TCloudRequestStandardSchemeFactory implements SchemeFactory {
    public TCloudRequestStandardScheme getScheme() {
      return new TCloudRequestStandardScheme();
    }
  }

  private static class TCloudRequestStandardScheme extends StandardScheme<TCloudRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TCloudRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ENTRY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.entry = new TCloudEntry();
              struct.entry.read(iprot);
              struct.setEntryIsSet(true);
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
          case 4: // CONTEXT
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map0 = iprot.readMapBegin();
                struct.context = new HashMap<String,String>(2*_map0.size);
                for (int _i1 = 0; _i1 < _map0.size; ++_i1)
                {
                  String _key2; // required
                  String _val3; // required
                  _key2 = iprot.readString();
                  _val3 = iprot.readString();
                  struct.context.put(_key2, _val3);
                }
                iprot.readMapEnd();
              }
              struct.setContextIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // CALLBACK
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.callback = new TCloudEntry();
              struct.callback.read(iprot);
              struct.setCallbackIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // LOGTRACK
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.logtrack = iprot.readBool();
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TCloudRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.entry != null) {
        oprot.writeFieldBegin(ENTRY_FIELD_DESC);
        struct.entry.write(oprot);
        oprot.writeFieldEnd();
      }
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
      if (struct.context != null) {
        oprot.writeFieldBegin(CONTEXT_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRING, struct.context.size()));
          for (Map.Entry<String, String> _iter4 : struct.context.entrySet())
          {
            oprot.writeString(_iter4.getKey());
            oprot.writeString(_iter4.getValue());
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.callback != null) {
        oprot.writeFieldBegin(CALLBACK_FIELD_DESC);
        struct.callback.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(LOGTRACK_FIELD_DESC);
      oprot.writeBool(struct.logtrack);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TCloudRequestTupleSchemeFactory implements SchemeFactory {
    public TCloudRequestTupleScheme getScheme() {
      return new TCloudRequestTupleScheme();
    }
  }

  private static class TCloudRequestTupleScheme extends TupleScheme<TCloudRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TCloudRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetEntry()) {
        optionals.set(0);
      }
      if (struct.isSetContentType()) {
        optionals.set(1);
      }
      if (struct.isSetContent()) {
        optionals.set(2);
      }
      if (struct.isSetContext()) {
        optionals.set(3);
      }
      if (struct.isSetCallback()) {
        optionals.set(4);
      }
      if (struct.isSetLogtrack()) {
        optionals.set(5);
      }
      oprot.writeBitSet(optionals, 6);
      if (struct.isSetEntry()) {
        struct.entry.write(oprot);
      }
      if (struct.isSetContentType()) {
        oprot.writeString(struct.contentType);
      }
      if (struct.isSetContent()) {
        oprot.writeBinary(struct.content);
      }
      if (struct.isSetContext()) {
        {
          oprot.writeI32(struct.context.size());
          for (Map.Entry<String, String> _iter5 : struct.context.entrySet())
          {
            oprot.writeString(_iter5.getKey());
            oprot.writeString(_iter5.getValue());
          }
        }
      }
      if (struct.isSetCallback()) {
        struct.callback.write(oprot);
      }
      if (struct.isSetLogtrack()) {
        oprot.writeBool(struct.logtrack);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TCloudRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(6);
      if (incoming.get(0)) {
        struct.entry = new TCloudEntry();
        struct.entry.read(iprot);
        struct.setEntryIsSet(true);
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
          org.apache.thrift.protocol.TMap _map6 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.context = new HashMap<String,String>(2*_map6.size);
          for (int _i7 = 0; _i7 < _map6.size; ++_i7)
          {
            String _key8; // required
            String _val9; // required
            _key8 = iprot.readString();
            _val9 = iprot.readString();
            struct.context.put(_key8, _val9);
          }
        }
        struct.setContextIsSet(true);
      }
      if (incoming.get(4)) {
        struct.callback = new TCloudEntry();
        struct.callback.read(iprot);
        struct.setCallbackIsSet(true);
      }
      if (incoming.get(5)) {
        struct.logtrack = iprot.readBool();
        struct.setLogtrackIsSet(true);
      }
    }
  }

}

