export async function invokePost(method, data) {
  const requestOptions = { method: "POST", body: data };
  const res = await fetch("/api/" + method, requestOptions);
  if (res.ok) return await res.json();
  return null;
}

export async function invokeGet(method) {
  const res = await fetch("/api/" + method);
  if (res.ok) return await res.json();
  return null;
}
