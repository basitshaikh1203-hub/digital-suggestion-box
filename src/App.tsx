import { type ChangeEvent, type FormEvent, type ReactNode, useMemo, useState } from 'react';
import { Route, Switch, Link, Router as WouterRouter, useLocation } from 'wouter';
import {
  ArrowLeft,
  ArrowRight,
  Check,
  Clipboard,
  ImagePlus,
  Info,
  Lightbulb,
  Menu,
  MessageSquare,
  Search,
  Send,
  ShieldCheck,
  Sparkles,
  ThumbsUp,
  Trash2,
  X,
} from 'lucide-react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { TooltipProvider } from '@/components/ui/tooltip';
import { Toaster } from '@/components/ui/toaster';
import { ErrorBoundary } from '@/components/error-boundary';
import { generateReferenceCode } from '@/lib/reference-code';
import {
  categories,
  floors,
  initialSuggestions,
  locations,
  rooms,
  type FeedbackType,
  type SuggestionRecord,
  type SuggestionStatus,
} from '@/lib/mock-data';

const queryClient = new QueryClient();

function Logo() {
  return (
    <Link href="/" className="flex items-center gap-3" data-testid="link-home-logo">
      <span className="grid size-10 place-items-center rounded-xl bg-[hsl(var(--primary))] text-[hsl(var(--primary-foreground))] shadow-sm">
        <MessageSquare size={19} strokeWidth={2.3} />
      </span>
      <span className="leading-none">
        <span className="block font-display text-[1.08rem] font-semibold tracking-[-.02em]">Digital Suggestion Box</span>
        <span className="mt-1 block font-mono-ui text-[9px] uppercase tracking-[.15em] text-[hsl(var(--muted-foreground))]">VPPCOE / student voice</span>
      </span>
    </Link>
  );
}

function Header() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [path] = useLocation();
  const links = [
    { href: '/submit', label: 'Share ' },
    { href: '/track', label: 'Track feedback' },
    { href: '/suggestions', label: 'Public suggestions' },
  ];
  return (
    <header className="relative z-20 border-b border-[hsl(var(--border)/.75)] bg-[hsl(var(--background)/.92)] backdrop-blur-md">
      <div className="mx-auto flex h-[76px] max-w-6xl items-center justify-between px-5 sm:px-8">
        <Logo />
        <nav className="hidden items-center gap-7 md:flex" aria-label="Main navigation">
          {links.map((link) => (
            <Link
              key={link.href}
              href={link.href}
              className={`text-sm font-semibold transition-colors hover:text-[hsl(var(--primary))] ${path === link.href ? 'text-[hsl(var(--primary))]' : 'text-[hsl(var(--muted-foreground))]'}`}
              data-testid={`link-nav-${link.label.toLowerCase().replaceAll(' ', '-')}`}
            >
              {link.label}
            </Link>
          ))}
          <Link href="/submit" className="rounded-full bg-[hsl(var(--accent))] px-5 py-2.5 text-sm font-bold text-[hsl(var(--accent-foreground))] transition-transform hover:-translate-y-0.5" data-testid="link-nav-submit">
            Submit anonymously
          </Link>
        </nav>
        <button className="rounded-lg p-2 md:hidden" onClick={() => setMenuOpen((open) => !open)} aria-label="Toggle navigation" data-testid="button-mobile-menu">
          {menuOpen ? <X size={22} /> : <Menu size={22} />}
        </button>
      </div>
      {menuOpen && (
        <nav className="animate-fade border-t border-[hsl(var(--border)/.7)] px-5 py-4 md:hidden" aria-label="Mobile navigation">
          <div className="mx-auto flex max-w-6xl flex-col gap-1">
            {links.map((link) => (
              <Link key={link.href} href={link.href} onClick={() => setMenuOpen(false)} className="rounded-lg px-3 py-3 text-sm font-semibold hover:bg-[hsl(var(--muted))]" data-testid={`link-mobile-${link.label.toLowerCase().replaceAll(' ', '-')}`}>
                {link.label}
              </Link>
            ))}
            <Link
  href="/admin/login"
  onClick={() => setMenuOpen(false)}
  className="rounded-lg px-3 py-3 text-sm font-semibold hover:bg-[hsl(var(--muted))]"
  data-testid="link-mobile-admin-login"
>
  Admin Login
</Link>
          </div>
        </nav>
      )}
    </header>
  );
}

function Footer() {
  return (
    <footer className="border-t border-[hsl(var(--border)/.8)] bg-[hsl(var(--secondary)/.42)]">
      <div className="mx-auto flex max-w-6xl flex-col gap-4 px-5 py-8 text-sm text-[hsl(var(--muted-foreground))] sm:flex-row sm:items-center sm:justify-between sm:px-8">
        <div>
          <p className="font-semibold text-[hsl(var(--foreground))]">Vasantdada Patil Pratishthan&apos;s College of Engineering</p>
          <p className="mt-1">A quieter way to make campus better.</p>
        </div>
        <p className="font-mono-ui text-[10px] uppercase tracking-[.12em]">Student service · prototype</p>
      </div>
    </footer>
  );
}

function Shell({ children }: { children: ReactNode }) {
  return <div className="min-h-[100dvh] bg-[hsl(var(--background))]"><Header />{children}<Footer /></div>;
}

function SectionKicker({ children }: { children: React.ReactNode }) {
  return <p className="mb-4 font-mono-ui text-[10px] font-bold uppercase tracking-[.18em] text-[hsl(var(--primary))]">{children}</p>;
}

function ButtonLink({ href, children, secondary = false, testId }: { href: string; children: React.ReactNode; secondary?: boolean; testId: string }) {
  return (
    <Link href={href} className={`inline-flex items-center justify-center gap-2 rounded-full px-5 py-3 text-sm font-bold transition-all hover:-translate-y-0.5 ${secondary ? 'border border-[hsl(var(--border))] bg-[hsl(var(--card))] text-[hsl(var(--foreground))] hover:border-[hsl(var(--primary)/.5)]' : 'bg-[hsl(var(--accent))] text-[hsl(var(--accent-foreground))] shadow-[0_5px_0_hsl(18_54%_43%)] hover:shadow-[0_3px_0_hsl(18_54%_43%)]'}`} data-testid={testId}>
      {children}
    </Link>
  );
}

function StatusBadge({ status }: { status: SuggestionStatus }) {
  const tones: Record<SuggestionStatus, string> = {
    Received: 'bg-[hsl(var(--muted))] text-[hsl(var(--muted-foreground))]',
    'Under review': 'bg-[hsl(42_80%_84%)] text-[hsl(34_52%_30%)]',
    'In progress': 'bg-[hsl(195_55%_84%)] text-[hsl(194_47%_29%)]',
    Resolved: 'bg-[hsl(157_42%_82%)] text-[hsl(161_44%_25%)]',
  };
  return <span className={`inline-flex rounded-full px-2.5 py-1 text-[11px] font-bold ${tones[status]}`}>{status}</span>;
}

function SuggestionCard({ record, voted, onVote }: { record: SuggestionRecord; voted: boolean; onVote: (id: string) => void }) {
  return (
    <article className="group flex flex-col rounded-2xl border border-[hsl(var(--card-border))] bg-[hsl(var(--card))] p-5 transition-transform hover:-translate-y-1 paper-shadow" data-testid={`card-suggestion-${record.id}`}>
      <div className="flex items-start justify-between gap-3">
        <div className="flex flex-wrap items-center gap-2">
          <span className="text-xs font-bold text-[hsl(var(--primary))]">{record.category}</span>
          <span className="text-[hsl(var(--border))]">/</span>
          <span className="text-xs text-[hsl(var(--muted-foreground))]">{record.location}</span>
        </div>
        <StatusBadge status={record.status} />
      </div>
      <p className="mt-4 flex-1 text-[15px] leading-7 text-[hsl(var(--foreground)/.86)]">{record.description}</p>
      <div className="mt-5 flex items-center justify-between border-t border-[hsl(var(--border)/.7)] pt-4">
        <span className="font-mono-ui text-[10px] uppercase tracking-[.11em] text-[hsl(var(--muted-foreground))]">{record.type}</span>
        <button onClick={() => onVote(record.id)} className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1.5 text-xs font-bold transition-colors ${voted ? 'bg-[hsl(var(--primary))] text-[hsl(var(--primary-foreground))]' : 'bg-[hsl(var(--muted))] text-[hsl(var(--muted-foreground))] hover:bg-[hsl(var(--primary)/.14)] hover:text-[hsl(var(--primary))]'}`} aria-pressed={voted} data-testid={`button-upvote-${record.id}`}>
          <ThumbsUp size={13} fill={voted ? 'currentColor' : 'none'} /> {record.votes + (voted ? 1 : 0)}
        </button>
      </div>
    </article>
  );
}

function Home({ records, votedIds, onVote }: { records: SuggestionRecord[]; votedIds: string[]; onVote: (id: string) => void }) {
  return (
    <Shell>
      <main>
        <section className="relative overflow-hidden border-b border-[hsl(var(--border)/.75)]">
          <div className="absolute inset-0 ink-grid opacity-55" />
          <div className="absolute -right-24 -top-36 size-[420px] rounded-full bg-[hsl(var(--accent)/.13)] blur-3xl" />
          <div className="relative mx-auto grid max-w-6xl items-center gap-14 px-5 py-20 sm:px-8 md:grid-cols-[1.07fr_.93fr] md:py-28">
            <div className="animate-rise">
              <div className="mb-8 flex items-center gap-3">
                <span className="grid size-8 place-items-center rounded-lg bg-[hsl(var(--primary))] text-[hsl(var(--primary-foreground))]"><Lightbulb size={16} /></span>
                <span className="font-mono-ui text-[10px] font-bold uppercase tracking-[.18em] text-[hsl(var(--primary))]">A student-led signal to campus</span>
              </div>
              <h1 className="max-w-[650px] font-display text-[clamp(3.2rem,7vw,6.3rem)] font-semibold leading-[.94] tracking-[-.06em] text-[hsl(var(--foreground))]">Your Voice.<br /><span className="text-[hsl(var(--primary))]">Your Ideas.</span><br />Our College.</h1>
              <p className="mt-8 max-w-lg text-lg leading-8 text-[hsl(var(--muted-foreground))]">A simple, anonymous place to share what could make Vasantdada Patil Pratishthan&apos;s College of Engineering a better place to learn and belong.</p>
              <div className="mt-9 flex flex-col gap-3 sm:flex-row">
                <ButtonLink href="/submit" testId="link-hero-submit">Share feedback <ArrowRight size={16} /></ButtonLink>
                <ButtonLink href="/track" secondary testId="link-hero-track">Track a submission</ButtonLink>
              </div>
              <div className="mt-9 flex items-center gap-2 text-xs text-[hsl(var(--muted-foreground))]"><ShieldCheck size={15} className="text-[hsl(var(--primary))]" /> No account. No name. No roll number.</div>
            </div>
            <div className="relative mx-auto w-full max-w-[420px] animate-rise delay-2">
              <div className="absolute -left-5 -top-5 hidden size-20 rounded-full border border-dashed border-[hsl(var(--accent)/.55)] sm:block" />
              <div className="relative rotate-2 rounded-[2rem] bg-[hsl(var(--primary))] p-3 shadow-[13px_17px_0_hsl(165_29%_71%/.32)]">
                <div className="rounded-[1.45rem] border border-[hsl(var(--primary-foreground)/.23)] bg-[hsl(var(--primary)/.88)] p-7 text-[hsl(var(--primary-foreground))]">
                  <div className="flex items-center justify-between"><span className="font-mono-ui text-[10px] uppercase tracking-[.15em] opacity-70">Field note 01</span><span className="size-2 rounded-full bg-[hsl(var(--accent))]" /></div>
                  <div className="my-14">
                    <p className="font-display text-4xl leading-tight">Small notes.<br />Real change.</p>
                    <p className="mt-5 max-w-[250px] text-sm leading-6 opacity-70">Tell us what&apos;s on your mind. We&apos;ll make sure it reaches the right people.</p>
                  </div>
                  <div className="flex items-end justify-between"><span className="font-mono-ui text-[9px] uppercase tracking-[.1em] opacity-55">Vasantdada Patil<br />Pratishthan&apos;s College</span><Send size={27} strokeWidth={1.5} /></div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="mx-auto grid max-w-6xl gap-10 px-5 py-20 sm:px-8 md:grid-cols-[.85fr_1.15fr] md:py-28">
          <div className="animate-rise"><SectionKicker>How it works</SectionKicker><h2 className="max-w-md font-display text-4xl leading-tight tracking-[-.04em] md:text-5xl">A direct line, without the awkwardness.</h2><p className="mt-5 max-w-sm leading-7 text-[hsl(var(--muted-foreground))]">You don&apos;t have to know who to email or how to phrase it. Just tell us what you see.</p></div>
          <div className="grid gap-0">
            {[
              ['01', 'Put it into words', 'Choose a type, add a little context, and share what you think should change.'],
              ['02', 'Keep your distance', 'We don’t ask for your name, contact details, or student information.'],
              ['03', 'Keep the reference', 'You’ll get a private code to check for updates whenever you want.'],
            ].map(([number, title, copy], index) => (
              <div key={number} className={`flex gap-5 border-t border-[hsl(var(--border))] py-6 animate-rise delay-${index + 1}`}>
                <span className="font-mono-ui text-xs text-[hsl(var(--accent))]">{number}</span><div><h3 className="font-semibold">{title}</h3><p className="mt-1 max-w-lg text-sm leading-6 text-[hsl(var(--muted-foreground))]">{copy}</p></div>
              </div>
            ))}
          </div>
        </section>

        <section className="bg-[hsl(var(--primary))] text-[hsl(var(--primary-foreground))]">
          <div className="mx-auto max-w-6xl px-5 py-20 sm:px-8 md:py-24">
            <div className="grid gap-10 md:grid-cols-[.8fr_1.2fr] md:items-end"><div><SectionKicker><span className="text-[hsl(var(--accent))]">Nothing is too small</span></SectionKicker><h2 className="font-display text-4xl leading-tight tracking-[-.04em] md:text-5xl">What can you share?</h2></div><div className="grid grid-cols-2 gap-x-8 gap-y-5 sm:grid-cols-4">{['A better timetable', 'A broken fan', 'A campus idea', 'A difficult moment'].map((item) => <div key={item} className="border-t border-[hsl(var(--primary-foreground)/.28)] pt-3 text-sm leading-5 text-[hsl(var(--primary-foreground)/.78)]">{item}</div>)}</div></div>
          </div>
        </section>

        <section className="mx-auto max-w-6xl px-5 py-20 sm:px-8 md:py-28">
          <div className="flex flex-col justify-between gap-5 sm:flex-row sm:items-end"><div><SectionKicker>In the open</SectionKicker><h2 className="font-display text-4xl tracking-[-.04em] md:text-5xl">What Students Are Saying</h2></div><Link href="/suggestions" className="inline-flex items-center gap-2 text-sm font-bold text-[hsl(var(--primary))]" data-testid="link-home-public-suggestions">View all suggestions <ArrowRight size={15} /></Link></div>
          <div className="mt-9 grid gap-5 md:grid-cols-2">{records.slice(0, 2).map((record) => <SuggestionCard key={record.id} record={record} voted={votedIds.includes(record.id)} onVote={onVote} />)}</div>
        </section>

        <section className="mx-5 mb-20 overflow-hidden rounded-[2rem] bg-[hsl(var(--accent))] sm:mx-8 md:mb-28">
          <div className="mx-auto flex max-w-6xl flex-col items-start gap-7 px-7 py-12 sm:px-12 md:flex-row md:items-center md:justify-between md:py-14"><div><p className="font-mono-ui text-[10px] font-bold uppercase tracking-[.18em] text-[hsl(var(--accent-foreground)/.65)]">Take the first step</p><h2 className="mt-3 max-w-xl font-display text-4xl leading-tight tracking-[-.04em] text-[hsl(var(--accent-foreground))]">Your next good idea could start here.</h2></div><ButtonLink href="/submit" secondary testId="link-home-final-submit">Share Anonymously <ArrowRight size={16} /></ButtonLink></div>
        </section>
      </main>
    </Shell>
  );
}

type UploadPreview = { id: string; name: string; url: string };
function SubmitPage({ onSubmit, records }: { onSubmit: (record: SuggestionRecord) => void; records: SuggestionRecord[] }) {
  const [, setLocation] = useLocation();
  const [type, setType] = useState<FeedbackType | ''>('');
  const [category, setCategory] = useState('');
  const [location, setLocationField] = useState('');
  const [room, setRoom] = useState('');
  const [floor, setFloor] = useState('');
  const [description, setDescription] = useState('');
  const [uploads, setUploads] = useState<UploadPreview[]>([]);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  function addFiles(event: ChangeEvent<HTMLInputElement>) {
    const files = Array.from(event.target.files ?? []);
    const accepted = ['image/jpeg', 'image/png', 'image/webp'];
    if (files.some((file) => !accepted.includes(file.type))) { setError('Please choose a JPG, JPEG, PNG, or WEBP image.'); return; }
    if (files.some((file) => file.size > 5 * 1024 * 1024)) { setError('Each image must be smaller than 5MB.'); return; }
    const available = 2 - uploads.length;
    setError('');
    setUploads((current) => [...current, ...files.slice(0, available).map((file) => ({ id: `${file.name}-${file.lastModified}`, name: file.name, url: URL.createObjectURL(file) }))]);
    event.target.value = '';
  }
  function removeFile(id: string) { setUploads((current) => { const item = current.find((file) => file.id === id); if (item) URL.revokeObjectURL(item.url); return current.filter((file) => file.id !== id); }); }
  function submit(event: FormEvent) {
    event.preventDefault();
    if (description.trim().length < 1) { setError('Please tell us what you would like us to know.'); return; }
    setError(''); setSubmitting(true);
    window.setTimeout(() => {
      const code = generateReferenceCode(records.map((record) => record.referenceCode));
      const fullLocation = location === 'Classroom' ? [location, room, floor].filter(Boolean).join(' · ') : location;
      onSubmit({ id: `local-${Date.now()}`, referenceCode: code, type: type || 'Suggestion', category: category || 'Other', location: fullLocation || 'Not specified', description: description.trim(), status: 'Received', votes: 0, createdAt: new Date().toISOString() });
      setSubmitting(false); setLocation('/submitted');
    }, 650);
  }
  return (
    <Shell>
      <main className="mx-auto max-w-6xl px-5 py-12 sm:px-8 md:py-20">
        <div className="mb-12 max-w-2xl animate-rise"><SectionKicker>Make a note</SectionKicker><h1 className="font-display text-5xl tracking-[-.05em] md:text-6xl">Share Your Feedback</h1><p className="mt-5 text-lg leading-8 text-[hsl(var(--muted-foreground))]">A useful suggestion can be one sentence. A difficult problem can take more space. Both belong here.</p></div>
        <form onSubmit={submit} className="grid gap-8 lg:grid-cols-[1fr_330px]">
          <div className="rounded-[1.5rem] border border-[hsl(var(--card-border))] bg-[hsl(var(--card))] p-5 paper-shadow sm:p-8">
            {error && <div className="mb-7 flex items-start gap-3 rounded-xl border border-[hsl(var(--destructive)/.35)] bg-[hsl(var(--destructive)/.08)] p-4 text-sm text-[hsl(var(--destructive))]" role="alert" data-testid="status-submit-error"><Info size={17} className="mt-0.5 shrink-0" />{error}</div>}
            <div className="grid gap-7">
              <FieldLabel label="What kind of feedback is this?" hint="Optional">
                <div className="grid grid-cols-3 gap-2">{(['Suggestion', 'Complaint', 'Problem'] as FeedbackType[]).map((item) => <button type="button" key={item} onClick={() => setType(type === item ? '' : item)} className={`rounded-xl border px-3 py-3 text-sm font-semibold transition-colors ${type === item ? 'border-[hsl(var(--primary))] bg-[hsl(var(--primary)/.1)] text-[hsl(var(--primary))]' : 'border-[hsl(var(--border))] hover:bg-[hsl(var(--muted))]'}`} data-testid={`button-feedback-type-${item.toLowerCase()}`}>{item}</button>)}</div>
              </FieldLabel>
              <FieldLabel label="Which area does this relate to?" hint="Optional">
                <select value={category} onChange={(event) => setCategory(event.target.value)} className="form-control" data-testid="select-category"><option value="">Choose a category</option>{categories.map((item) => <option key={item} value={item}>{item}</option>)}</select>
              </FieldLabel>
              <FieldLabel label="Where on campus?" hint="Optional">
                <select value={location} onChange={(event) => { setLocationField(event.target.value); setRoom(''); setFloor(''); }} className="form-control" data-testid="select-location"><option value="">Choose a location</option>{locations.map((item) => <option key={item} value={item}>{item}</option>)}</select>
              </FieldLabel>
              {location === 'Classroom' && <div className="grid gap-4 sm:grid-cols-2 animate-fade"><div><label className="mb-2 block text-xs font-bold text-[hsl(var(--muted-foreground))]" htmlFor="room">Classroom</label><select id="room" value={room} onChange={(event) => setRoom(event.target.value)} className="form-control" data-testid="select-classroom"><option value="">Choose room</option>{rooms.map((item) => <option key={item} value={item}>{item}</option>)}</select></div><div><label className="mb-2 block text-xs font-bold text-[hsl(var(--muted-foreground))]" htmlFor="floor">Floor</label><select id="floor" value={floor} onChange={(event) => setFloor(event.target.value)} className="form-control" data-testid="select-floor"><option value="">Choose floor</option>{floors.map((item) => <option key={item} value={item}>{item}</option>)}</select></div></div>}
              <FieldLabel label="Tell us what is on your mind." hint="Required">
                <div className="relative"><textarea value={description} maxLength={1000} onChange={(event) => setDescription(event.target.value)} rows={8} placeholder="What happened, or what would you like to see change?" className="form-control min-h-[190px] resize-y pr-16" data-testid="input-description" required /><span className="absolute bottom-3 right-3 font-mono-ui text-[10px] text-[hsl(var(--muted-foreground))]" data-testid="text-character-count">{description.length}/1000</span></div>
              </FieldLabel>
              <FieldLabel label="Add an image" hint="Optional · up to 2 images, 5MB each">
                <div className="rounded-xl border border-dashed border-[hsl(var(--border))] bg-[hsl(var(--background)/.5)] p-5"><label className="flex cursor-pointer items-center gap-3 text-sm font-semibold text-[hsl(var(--primary))]" htmlFor="image-upload"><span className="grid size-9 place-items-center rounded-lg bg-[hsl(var(--primary)/.1)]"><ImagePlus size={17} /></span>Choose image files<input id="image-upload" type="file" accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp" multiple disabled={uploads.length >= 2} onChange={addFiles} className="sr-only" data-testid="input-image-upload" /></label>{uploads.length > 0 && <div className="mt-4 grid grid-cols-2 gap-3">{uploads.map((file) => <div key={file.id} className="relative overflow-hidden rounded-lg border border-[hsl(var(--border))] bg-[hsl(var(--muted))]"><img src={file.url} alt={`Preview of ${file.name}`} className="h-28 w-full object-cover" data-testid={`img-upload-preview-${file.id}`} /><button type="button" onClick={() => removeFile(file.id)} className="absolute right-2 top-2 rounded-full bg-[hsl(var(--foreground)/.78)] p-1.5 text-[hsl(var(--background))]" aria-label={`Remove ${file.name}`} data-testid={`button-remove-upload-${file.id}`}><Trash2 size={13} /></button><p className="truncate px-2 py-1.5 text-[10px] text-[hsl(var(--muted-foreground))]">{file.name}</p></div>)}</div>}</div>
              </FieldLabel>
            </div>
            <div className="mt-8 flex flex-col-reverse items-stretch justify-between gap-4 border-t border-[hsl(var(--border))] pt-6 sm:flex-row sm:items-center"><Link href="/" className="inline-flex items-center justify-center gap-2 text-sm font-bold text-[hsl(var(--muted-foreground))]" data-testid="link-submit-cancel"><ArrowLeft size={15} /> Back home</Link><button type="submit" disabled={submitting} className="inline-flex items-center justify-center gap-2 rounded-full bg-[hsl(var(--primary))] px-6 py-3 text-sm font-bold text-[hsl(var(--primary-foreground))] transition-transform hover:-translate-y-0.5 disabled:cursor-wait disabled:opacity-60" data-testid="button-submit-feedback">{submitting ? 'Sending…' : 'Submit anonymously'}<Send size={15} /></button></div>
          </div>
          <aside className="h-fit rounded-[1.5rem] border border-[hsl(var(--border))] bg-[hsl(var(--secondary)/.55)] p-6 animate-rise delay-2"><ShieldCheck size={22} className="text-[hsl(var(--primary))]" /><h2 className="mt-4 font-display text-2xl">Your privacy matters.</h2><p className="mt-3 text-sm leading-6 text-[hsl(var(--muted-foreground))]">This form does not ask for your name, email, phone number, or student details. Keep the reference code after submitting so you can follow up.</p><div className="mt-7 border-t border-[hsl(var(--border))] pt-5"><p className="font-mono-ui text-[10px] uppercase tracking-[.12em] text-[hsl(var(--muted-foreground))]">Good to know</p><p className="mt-2 text-sm font-semibold leading-6">You can skip every optional field. The description is the only thing we need.</p></div></aside>
        </form>
      </main>
    </Shell>
  );
}

function FieldLabel({ label, hint, children }: { label: string; hint: string; children: ReactNode }) {
  return <div><div className="mb-2 flex items-baseline justify-between gap-4"><label className="text-sm font-bold">{label}</label><span className="font-mono-ui text-[9px] uppercase tracking-[.1em] text-[hsl(var(--muted-foreground))]">{hint}</span></div>{children}</div>;
}

function SubmittedPage({ record }: { record?: SuggestionRecord }) {
  const [, setLocation] = useLocation();
  const [copied, setCopied] = useState(false);
  const code = record?.referenceCode ?? 'SB-X7K4P9';
  async function copyCode() {
    try { await navigator.clipboard.writeText(code); } catch { /* clipboard may be unavailable in preview */ }
    setCopied(true); window.setTimeout(() => setCopied(false), 1800);
  }
  return <Shell><main className="mx-auto flex max-w-3xl flex-col items-center px-5 py-20 text-center sm:px-8 md:py-28"><div className="relative grid size-20 place-items-center rounded-full bg-[hsl(var(--primary))] text-[hsl(var(--primary-foreground))] animate-rise"><Check size={35} strokeWidth={2.5} /><span className="absolute inset-[-9px] rounded-full border border-dashed border-[hsl(var(--primary)/.35)]" /></div><SectionKicker>Message received</SectionKicker><h1 className="font-display text-5xl tracking-[-.05em] md:text-6xl">Thank you for speaking up.</h1><p className="mt-5 max-w-lg text-lg leading-8 text-[hsl(var(--muted-foreground))]">Your feedback has been saved. Keep this reference code somewhere safe to check its progress later.</p><div className="mt-10 w-full max-w-md rounded-2xl border border-[hsl(var(--border))] bg-[hsl(var(--card))] p-6 paper-shadow"><p className="font-mono-ui text-[10px] uppercase tracking-[.16em] text-[hsl(var(--muted-foreground))]">Your reference code</p><p className="mt-3 font-mono-ui text-3xl font-bold tracking-[.14em] text-[hsl(var(--primary))]" data-testid="text-reference-code">{code}</p><button onClick={copyCode} className="mt-5 inline-flex items-center gap-2 rounded-full border border-[hsl(var(--border))] px-4 py-2 text-xs font-bold hover:bg-[hsl(var(--muted))]" data-testid="button-copy-reference">{copied ? <Check size={14} /> : <Clipboard size={14} />}{copied ? 'Copied to clipboard' : 'Copy code'}</button></div><p className="mt-6 flex items-center gap-2 text-xs text-[hsl(var(--muted-foreground))]"><ShieldCheck size={14} className="text-[hsl(var(--primary))]" /> You do not need to provide your identity to track this feedback.</p><div className="mt-10 flex w-full flex-col justify-center gap-3 sm:flex-row"><button onClick={() => setLocation('/track')} className="inline-flex items-center justify-center gap-2 rounded-full bg-[hsl(var(--accent))] px-5 py-3 text-sm font-bold text-[hsl(var(--accent-foreground))]" data-testid="button-track-submitted">Track this feedback <ArrowRight size={15} /></button><Link href="/" className="inline-flex items-center justify-center gap-2 rounded-full border border-[hsl(var(--border))] bg-[hsl(var(--card))] px-5 py-3 text-sm font-bold" data-testid="link-submitted-home">Back to home</Link></div></main></Shell>;
}

function TrackPage({ records, prefillCode }: { records: SuggestionRecord[]; prefillCode?: string }) {
  const [code, setCode] = useState(prefillCode ?? '');
  const [searched, setSearched] = useState(false);
  const record = useMemo(() => records.find((item) => item.referenceCode === code.trim().toUpperCase()), [code, records]);
  function submit(event: FormEvent) { event.preventDefault(); setSearched(true); }
  return <Shell><main className="mx-auto max-w-5xl px-5 py-12 sm:px-8 md:py-20"><div className="mx-auto max-w-2xl text-center animate-rise"><SectionKicker>Keep the thread</SectionKicker><h1 className="font-display text-5xl tracking-[-.05em] md:text-6xl">Track Your Feedback</h1><p className="mt-5 text-lg leading-8 text-[hsl(var(--muted-foreground))]">Enter the reference code you received after submitting. No name or account needed.</p><form onSubmit={submit} className="mx-auto mt-9 flex max-w-md flex-col gap-3 sm:flex-row"><div className="relative flex-1"><Search className="absolute left-4 top-1/2 -translate-y-1/2 text-[hsl(var(--muted-foreground))]" size={17} /><input value={code} onChange={(event) => { setCode(event.target.value.toUpperCase()); setSearched(false); }} placeholder="SB-X7K4P9" className="form-control pl-11 font-mono-ui uppercase" aria-label="Reference code" data-testid="input-reference-code" /></div><button className="rounded-full bg-[hsl(var(--primary))] px-6 py-3 text-sm font-bold text-[hsl(var(--primary-foreground))]" type="submit" data-testid="button-track-feedback">Look up</button></form></div>{searched && !record && <div className="mx-auto mt-12 max-w-2xl rounded-2xl border border-[hsl(var(--accent)/.35)] bg-[hsl(var(--accent)/.09)] p-7 text-center animate-rise" role="alert" data-testid="status-track-not-found"><div className="mx-auto grid size-11 place-items-center rounded-full bg-[hsl(var(--accent)/.2)]"><Search size={20} /></div><h2 className="mt-4 font-display text-2xl">We couldn&apos;t find that code.</h2><p className="mt-2 text-sm leading-6 text-[hsl(var(--muted-foreground))]">Check the letters and numbers, then try again. Reference codes look like SB-X7K4P9.</p></div>}{record && <div className="mx-auto mt-12 max-w-2xl rounded-[1.5rem] border border-[hsl(var(--card-border))] bg-[hsl(var(--card))] p-6 paper-shadow sm:p-8 animate-rise" data-testid={`card-track-result-${record.id}`}><div className="flex flex-col gap-5 border-b border-[hsl(var(--border))] pb-6 sm:flex-row sm:items-start sm:justify-between"><div><p className="font-mono-ui text-[10px] uppercase tracking-[.14em] text-[hsl(var(--muted-foreground))]">Reference code</p><p className="mt-2 font-mono-ui text-lg font-bold tracking-[.12em] text-[hsl(var(--primary))]">{record.referenceCode}</p></div><StatusBadge status={record.status} /></div><div className="grid gap-5 border-b border-[hsl(var(--border))] py-6 sm:grid-cols-3"><Meta label="Type" value={record.type} /><Meta label="Category" value={record.category} /><Meta label="Location" value={record.location} /></div><div className="py-6"><p className="font-mono-ui text-[10px] uppercase tracking-[.13em] text-[hsl(var(--muted-foreground))]">Your feedback</p><p className="mt-3 leading-7">{record.description}</p></div>{record.response ? <div className="rounded-xl bg-[hsl(var(--secondary)/.7)] p-5"><p className="flex items-center gap-2 font-mono-ui text-[10px] uppercase tracking-[.13em] text-[hsl(var(--primary))]"><MessageSquare size={14} /> College response</p><p className="mt-3 text-sm leading-6">{record.response}</p></div> : <div className="flex items-start gap-3 rounded-xl bg-[hsl(var(--muted)/.72)] p-5 text-sm leading-6 text-[hsl(var(--muted-foreground))]"><Info size={16} className="mt-0.5 shrink-0" />There is no response yet. Please check back later.</div>}</div>}</main></Shell>;
}

function Meta({ label, value }: { label: string; value: string }) {
  return <div><p className="font-mono-ui text-[9px] uppercase tracking-[.12em] text-[hsl(var(--muted-foreground))]">{label}</p><p className="mt-1 text-sm font-semibold">{value || 'Not specified'}</p></div>;
}

function SuggestionsPage({ records, votedIds, onVote }: { records: SuggestionRecord[]; votedIds: string[]; onVote: (id: string) => void }) {
  return <Shell><main className="mx-auto max-w-6xl px-5 py-12 sm:px-8 md:py-20"><div className="flex flex-col justify-between gap-7 md:flex-row md:items-end animate-rise"><div className="max-w-2xl"><SectionKicker>Shared, anonymously</SectionKicker><h1 className="font-display text-5xl tracking-[-.05em] md:text-6xl">What Students Are Saying</h1><p className="mt-5 text-lg leading-8 text-[hsl(var(--muted-foreground))]">A window into the ideas and issues students have chosen to share publicly. No identifying details are shown.</p></div><ButtonLink href="/submit" testId="link-suggestions-submit">Add your voice <ArrowRight size={15} /></ButtonLink></div><div className="mt-14 grid gap-5 md:grid-cols-2">{records.map((record, index) => <div key={record.id} className={`animate-rise delay-${Math.min(index + 1, 4)}`}><SuggestionCard record={record} voted={votedIds.includes(record.id)} onVote={onVote} /></div>)}</div>{records.length === 0 && <div className="mt-12 rounded-2xl border border-dashed border-[hsl(var(--border))] p-14 text-center"><Sparkles className="mx-auto text-[hsl(var(--primary))]" /><h2 className="mt-4 font-display text-2xl">The board is waiting for its first note.</h2><p className="mt-2 text-sm text-[hsl(var(--muted-foreground))]">Share something that could make campus better.</p></div>}<div className="mt-12 flex items-center justify-center gap-2 text-xs text-[hsl(var(--muted-foreground))]"><ShieldCheck size={14} className="text-[hsl(var(--primary))]" /> All posts are anonymous and moderated before appearing here.</div></main></Shell>;
}

function AdminLoginPage() {
  const [, setLocation] = useLocation();
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  function handleLogin(event: FormEvent) {
    event.preventDefault();

    if (password === 'zxcvbnm') {
      setError('');
      setLocation('/admin/dashboard');
    } else {
      setError('Incorrect password.');
    }
  }

  return (
    <Shell>
      <main className="mx-auto flex min-h-[70dvh] max-w-md flex-col justify-center px-5 py-16 sm:px-8">
        <div className="mb-8 text-center">
          <SectionKicker>Administration</SectionKicker>
          <h1 className="font-display text-5xl tracking-[-.05em]">
            Admin Login
          </h1>
          <p className="mt-4 text-sm leading-6 text-[hsl(var(--muted-foreground))]">
            Sign in to manage student feedback and suggestions.
          </p>
        </div>

        <form
          onSubmit={handleLogin}
          className="rounded-[1.5rem] border border-[hsl(var(--card-border))] bg-[hsl(var(--card))] p-6 paper-shadow sm:p-8"
        >
          {error && (
            <div className="mb-5 rounded-xl border border-[hsl(var(--destructive)/.35)] bg-[hsl(var(--destructive)/.08)] p-4 text-sm text-[hsl(var(--destructive))]">
              {error}
            </div>
          )}

          <label className="mb-2 block text-sm font-bold" htmlFor="admin-password">
            Password
          </label>

          <input
            id="admin-password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            placeholder="Enter admin password"
            className="form-control"
            required
          />

          <button
            type="submit"
            className="mt-6 w-full rounded-full bg-[hsl(var(--primary))] px-6 py-3 text-sm font-bold text-[hsl(var(--primary-foreground))] transition-transform hover:-translate-y-0.5"
          >
            Login
          </button>

          <Link
            href="/"
            className="mt-4 flex items-center justify-center gap-2 text-sm font-bold text-[hsl(var(--muted-foreground))]"
          >
            <ArrowLeft size={15} />
            Back home
          </Link>
        </form>
      </main>
    </Shell>
  );
}
function AdminDashboardPage({
  records,
  onUpdateRecord,
}: {
  records: SuggestionRecord[];
  onUpdateRecord: (
    id: string,
    status: SuggestionStatus,
    response: string,
  ) => void;
}) {
  const [, setLocation] = useLocation();

  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [editedStatuses, setEditedStatuses] = useState<
    Record<string, SuggestionStatus>
  >({});
  const [responses, setResponses] = useState<Record<string, string>>({});

  function getStatus(record: SuggestionRecord) {
    return editedStatuses[record.id] ?? record.status;
  }

  function getResponse(record: SuggestionRecord) {
    return responses[record.id] ?? record.response ?? '';
  }

  function updateStatus(id: string, status: SuggestionStatus) {
    setEditedStatuses((current) => ({
      ...current,
      [id]: status,
    }));
  }

  function updateResponse(id: string, response: string) {
    setResponses((current) => ({
      ...current,
      [id]: response,
    }));
  }

  return (
    <Shell>
      <main className="mx-auto max-w-6xl px-5 py-12 sm:px-8 md:py-20">
        <div className="flex flex-col justify-between gap-6 sm:flex-row sm:items-end">
          <div>
            <SectionKicker>Administration</SectionKicker>
            <h1 className="font-display text-5xl tracking-[-.05em] md:text-6xl">
              Admin Dashboard
            </h1>
            <p className="mt-4 max-w-xl text-lg leading-8 text-[hsl(var(--muted-foreground))]">
              Review student feedback, monitor its status, and keep track of
              what students are asking for.
            </p>
          </div>

          <button
            onClick={() => setLocation('/')}
            className="inline-flex items-center justify-center gap-2 rounded-full border border-[hsl(var(--border))] bg-[hsl(var(--card))] px-5 py-3 text-sm font-bold hover:bg-[hsl(var(--muted))]"
          >
            <ArrowLeft size={15} />
            Back to website
          </button>
        </div>

        <div className="mt-10 grid gap-5 sm:grid-cols-3">
          <div className="rounded-2xl border border-[hsl(var(--border))] bg-[hsl(var(--card))] p-6 paper-shadow">
            <p className="font-mono-ui text-[10px] uppercase tracking-[.13em] text-[hsl(var(--muted-foreground))]">
              Total feedback
            </p>
            <p className="mt-3 font-display text-4xl">
              {records.length}
            </p>
          </div>

          <div className="rounded-2xl border border-[hsl(var(--border))] bg-[hsl(var(--card))] p-6 paper-shadow">
            <p className="font-mono-ui text-[10px] uppercase tracking-[.13em] text-[hsl(var(--muted-foreground))]">
              Under review
            </p>
            <p className="mt-3 font-display text-4xl">
              {records.filter((record) => record.status === 'Under review').length}
            </p>
          </div>

          <div className="rounded-2xl border border-[hsl(var(--border))] bg-[hsl(var(--card))] p-6 paper-shadow">
            <p className="font-mono-ui text-[10px] uppercase tracking-[.13em] text-[hsl(var(--muted-foreground))]">
              Resolved
            </p>
            <p className="mt-3 font-display text-4xl">
              {records.filter((record) => record.status === 'Resolved').length}
            </p>
          </div>
        </div>

        <section className="mt-10">
          <div className="mb-5">
            <SectionKicker>Recent submissions</SectionKicker>
            <h2 className="font-display text-3xl tracking-[-.03em]">
              Student feedback
            </h2>
          </div>

          <div className="overflow-hidden rounded-2xl border border-[hsl(var(--border))] bg-[hsl(var(--card))] paper-shadow">
            {records.length === 0 ? (
              <div className="p-10 text-center">
                <p className="font-display text-2xl">
                  No feedback yet.
                </p>
                <p className="mt-2 text-sm text-[hsl(var(--muted-foreground))]">
                  Student submissions will appear here.
                </p>
              </div>
            ) : (
              <div className="divide-y divide-[hsl(var(--border))]">
                {records.map((record) => {
  const isOpen = selectedId === record.id;
  const status = getStatus(record);
  const response = getResponse(record);

  return (
    <div key={record.id} className="p-5">
      <button
        type="button"
        onClick={() => setSelectedId(isOpen ? null : record.id)}
        className="w-full text-left"
      >
        <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
          <div className="min-w-0">
            <div className="flex flex-wrap items-center gap-2">
              <span className="font-mono-ui text-xs font-bold text-[hsl(var(--primary))]">
                {record.referenceCode}
              </span>

              <StatusBadge status={status} />

              {record.images && record.images.length > 0 && (
                <span className="rounded-full bg-[hsl(var(--muted))] px-2.5 py-1 text-[11px] font-bold">
                  {record.images.length} image
                  {record.images.length > 1 ? 's' : ''}
                </span>
              )}
            </div>

            <p className="mt-3 text-sm font-semibold">
              {record.type} · {record.category}
            </p>

            <p className="mt-2 text-sm leading-6 text-[hsl(var(--muted-foreground))]">
              {record.description}
            </p>

            <p className="mt-2 text-xs text-[hsl(var(--muted-foreground))]">
              Location: {record.location}
            </p>
          </div>

          <div className="shrink-0 text-xs text-[hsl(var(--muted-foreground))]">
            {new Date(record.createdAt).toLocaleDateString()}
          </div>
        </div>
      </button>

      {isOpen && (
        <div className="mt-6 border-t border-[hsl(var(--border))] pt-6">
          <div className="grid gap-6 lg:grid-cols-2">
            <div>
              <p className="font-mono-ui text-[10px] font-bold uppercase tracking-[.13em] text-[hsl(var(--muted-foreground))]">
                Uploaded images
              </p>

              {record.images && record.images.length > 0 ? (
                <div className="mt-3 grid gap-3 sm:grid-cols-2">
                  {record.images.map((image) => (
                    <div
                      key={image.id}
                      className="overflow-hidden rounded-xl border border-[hsl(var(--border))]"
                    >
                      <img
                        src={image.url}
                        alt={image.name}
                        className="h-52 w-full object-cover"
                      />

                      <p className="truncate px-3 py-2 text-xs text-[hsl(var(--muted-foreground))]">
                        {image.name}
                      </p>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="mt-3 rounded-xl bg-[hsl(var(--muted)/.7)] p-5 text-sm text-[hsl(var(--muted-foreground))]">
                  No image was attached to this submission.
                </div>
              )}
            </div>

            <div className="space-y-6">
              <div>
                <label
                  htmlFor={`status-${record.id}`}
                  className="font-mono-ui text-[10px] font-bold uppercase tracking-[.13em] text-[hsl(var(--muted-foreground))]"
                >
                  Update status
                </label>

                <select
                  id={`status-${record.id}`}
                  value={status}
                  onChange={(event) =>
                    updateStatus(
                      record.id,
                      event.target.value as SuggestionStatus,
                    )
                  }
                  className="form-control mt-3"
                >
                  <option value="Received">Received</option>
                  <option value="Under review">Under review</option>
                  <option value="In progress">In progress</option>
                  <option value="Resolved">Resolved</option>
                </select>
              </div>

              <div>
                <label
                  htmlFor={`response-${record.id}`}
                  className="font-mono-ui text-[10px] font-bold uppercase tracking-[.13em] text-[hsl(var(--muted-foreground))]"
                >
                  College response
                </label>

                <textarea
                  id={`response-${record.id}`}
                  value={response}
                  onChange={(event) =>
                    updateResponse(record.id, event.target.value)
                  }
                  rows={6}
                  placeholder="Write a response that the student can see when they track this feedback."
                  className="form-control mt-3 resize-y"
                />

                <button
                  type="button"
                  onClick={() => {
                    onUpdateRecord(record.id, status, response);

                    setEditedStatuses((current) => {
                      const next = { ...current };
                      delete next[record.id];
                      return next;
                    });

                    setResponses((current) => {
                      const next = { ...current };
                      delete next[record.id];
                      return next;
                    });
                  }}
                  className="mt-3 rounded-full bg-[hsl(var(--primary))] px-5 py-2.5 text-sm font-bold text-[hsl(var(--primary-foreground))]"
                >
                  Save changes
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
})}
              </div>
            )}
          </div>
        </section>
      </main>
    </Shell>
  );
}
function NotFound() {
  return <Shell><main className="mx-auto flex min-h-[60dvh] max-w-xl flex-col items-center justify-center px-5 text-center"><span className="font-mono-ui text-xs text-[hsl(var(--accent))]">404 / not here</span><h1 className="mt-4 font-display text-5xl">This page took a wrong turn.</h1><p className="mt-4 text-[hsl(var(--muted-foreground))]">The page you are looking for does not exist.</p><ButtonLink href="/" testId="link-not-found-home">Back home</ButtonLink></main></Shell>;
}

function RouterContent({
  records,
  votedIds,
  onVote,
  onSubmit,
  onUpdateRecord,
  lastSubmitted,
}: {
  records: SuggestionRecord[];
  votedIds: string[];
  onVote: (id: string) => void;
  onSubmit: (record: SuggestionRecord) => void;
  onUpdateRecord: (
    id: string,
    status: SuggestionStatus,
    response: string,
  ) => void;
  lastSubmitted?: SuggestionRecord;
}) {
  const [location] = useLocation();
  return <ErrorBoundary resetKey={location}><Switch><Route path="/" component={() => <Home records={records} votedIds={votedIds} onVote={onVote} />} /><Route path="/submit" component={() => <SubmitPage records={records} onSubmit={onSubmit} />} /><Route path="/submitted" component={() => <SubmittedPage record={lastSubmitted} />} /><Route path="/track" component={() => <TrackPage records={records} prefillCode={lastSubmitted?.referenceCode} />} /><Route path="/suggestions" component={() => <SuggestionsPage records={records} votedIds={votedIds} onVote={onVote} />} /><Route path="/admin/login" component={AdminLoginPage} /><Route
  path="/admin/dashboard"
  component={() => (
    <AdminDashboardPage
      records={records}
      onUpdateRecord={onUpdateRecord}
    />
  )}
/><Route component={NotFound} /></Switch></ErrorBoundary>;
}

function App() {
  const [records, setRecords] = useState<SuggestionRecord[]>(initialSuggestions);
  const [votedIds, setVotedIds] = useState<string[]>([]);
  const [lastSubmitted, setLastSubmitted] = useState<SuggestionRecord>();
  function onVote(id: string) { setVotedIds((current) => current.includes(id) ? current.filter((item) => item !== id) : [...current, id]); }
  function onSubmit(record: SuggestionRecord) { setRecords((current) => [record, ...current]); setLastSubmitted(record); }
  function onUpdateRecord(
  id: string,
  status: SuggestionStatus,
  response: string,
) {
  setRecords((current) =>
    current.map((record) =>
      record.id === id
        ? {
            ...record,
            status,
            response: response.trim() || undefined,
          }
        : record,
    ),
  );
}
  return <QueryClientProvider client={queryClient}><TooltipProvider><WouterRouter base={import.meta.env.BASE_URL.replace(/\/$/, '')}><RouterContent
  records={records}
  votedIds={votedIds}
  onVote={onVote}
  onSubmit={onSubmit}
  onUpdateRecord={onUpdateRecord}
  lastSubmitted={lastSubmitted}
/></WouterRouter><Toaster /></TooltipProvider></QueryClientProvider>;
}

export default App;