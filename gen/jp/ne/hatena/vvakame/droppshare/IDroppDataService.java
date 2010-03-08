/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/vvakame/work/DroppShare/src/jp/ne/hatena/vvakame/droppshare/IDroppDataService.aidl
 */
package jp.ne.hatena.vvakame.droppshare;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
public interface IDroppDataService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements jp.ne.hatena.vvakame.droppshare.IDroppDataService
{
private static final java.lang.String DESCRIPTOR = "jp.ne.hatena.vvakame.droppshare.IDroppDataService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an IDroppDataService interface,
 * generating a proxy if needed.
 */
public static jp.ne.hatena.vvakame.droppshare.IDroppDataService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof jp.ne.hatena.vvakame.droppshare.IDroppDataService))) {
return ((jp.ne.hatena.vvakame.droppshare.IDroppDataService)iin);
}
return new jp.ne.hatena.vvakame.droppshare.IDroppDataService.Stub.Proxy(obj);
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
case TRANSACTION_showToast:
{
data.enforceInterface(DESCRIPTOR);
this.showToast();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements jp.ne.hatena.vvakame.droppshare.IDroppDataService
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
public void showToast() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_showToast, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_showToast = (IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void showToast() throws android.os.RemoteException;
}
