/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/vvakame/work/DroppShare/src/net/vvakame/droppshare/IDroppServiceCallback.aidl
 */
package net.vvakame.droppshare;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
import java.util.List;
public interface IDroppServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.vvakame.droppshare.IDroppServiceCallback
{
private static final java.lang.String DESCRIPTOR = "net.vvakame.droppshare.IDroppServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an IDroppServiceCallback interface,
 * generating a proxy if needed.
 */
public static net.vvakame.droppshare.IDroppServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.vvakame.droppshare.IDroppServiceCallback))) {
return ((net.vvakame.droppshare.IDroppServiceCallback)iin);
}
return new net.vvakame.droppshare.IDroppServiceCallback.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_pushAppDataList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<net.vvakame.droppshare.AppData> _arg0;
_arg0 = data.createTypedArrayList(net.vvakame.droppshare.AppData.CREATOR);
this.pushAppDataList(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements net.vvakame.droppshare.IDroppServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void pushAppDataList(java.util.List<net.vvakame.droppshare.AppData> appDataList) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeTypedList(appDataList);
mRemote.transact(Stub.TRANSACTION_pushAppDataList, _data, null, IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_pushAppDataList = (IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void pushAppDataList(java.util.List<net.vvakame.droppshare.AppData> appDataList) throws android.os.RemoteException;
}
