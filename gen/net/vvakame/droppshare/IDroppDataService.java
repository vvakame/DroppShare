/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/vvakame/work/DroppShare/src/net/vvakame/droppshare/IDroppDataService.aidl
 */
package net.vvakame.droppshare;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
import java.util.List;
public interface IDroppDataService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements net.vvakame.droppshare.IDroppDataService
{
private static final java.lang.String DESCRIPTOR = "net.vvakame.droppshare.IDroppDataService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an IDroppDataService interface,
 * generating a proxy if needed.
 */
public static net.vvakame.droppshare.IDroppDataService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof net.vvakame.droppshare.IDroppDataService))) {
return ((net.vvakame.droppshare.IDroppDataService)iin);
}
return new net.vvakame.droppshare.IDroppDataService.Stub.Proxy(obj);
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
case TRANSACTION_getAppDataList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<net.vvakame.droppshare.AppData> _result = this.getAppDataList();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
net.vvakame.droppshare.IDroppServiceCallback _arg0;
_arg0 = net.vvakame.droppshare.IDroppServiceCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
net.vvakame.droppshare.IDroppServiceCallback _arg0;
_arg0 = net.vvakame.droppshare.IDroppServiceCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements net.vvakame.droppshare.IDroppDataService
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
public java.util.List<net.vvakame.droppshare.AppData> getAppDataList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<net.vvakame.droppshare.AppData> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAppDataList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(net.vvakame.droppshare.AppData.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void registerCallback(net.vvakame.droppshare.IDroppServiceCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregisterCallback(net.vvakame.droppshare.IDroppServiceCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getAppDataList = (IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_registerCallback = (IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_unregisterCallback = (IBinder.FIRST_CALL_TRANSACTION + 2);
}
public java.util.List<net.vvakame.droppshare.AppData> getAppDataList() throws android.os.RemoteException;
public void registerCallback(net.vvakame.droppshare.IDroppServiceCallback callback) throws android.os.RemoteException;
public void unregisterCallback(net.vvakame.droppshare.IDroppServiceCallback callback) throws android.os.RemoteException;
}
