const alphabet = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';

export function generateReferenceCode(existingCodes: string[] = []): string {
  let code = '';
  do {
    const bytes = new Uint8Array(6);
    crypto.getRandomValues(bytes);
    code = `SB-${Array.from(bytes, (byte) => alphabet[byte % alphabet.length]).join('')}`;
  } while (existingCodes.includes(code));
  return code;
}