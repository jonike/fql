package catdata.fqlpp;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class FnExp implements Serializable {

	
	public static class ApplyTrans extends FnExp {
		public String f;
		public SetExp set;
		
		public ApplyTrans(String f, SetExp set) {
			this.f = f;
			this.set = set;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((f == null) ? 0 : f.hashCode());
			result = prime * result + ((set == null) ? 0 : set.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ApplyTrans other = (ApplyTrans) obj;
			if (f == null) {
				if (other.f != null)
					return false;
			} else if (!f.equals(other.f))
				return false;
			if (set == null) {
				if (other.set != null)
					return false;
			} else if (!set.equals(other.set))
				return false;
			return true;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}	
		
	}
	
	public static class Apply extends FnExp {
		public String f;
		public FnExp set;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((f == null) ? 0 : f.hashCode());
			result = prime * result + ((set == null) ? 0 : set.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Apply other = (Apply) obj;
			if (f == null) {
				if (other.f != null)
					return false;
			} else if (!f.equals(other.f))
				return false;
			if (set == null) {
				if (other.set != null)
					return false;
			} else if (!set.equals(other.set))
				return false;
			return true;
		}

		public Apply(String f, FnExp set) {
			this.f = f;
			this.set = set;
		}
		
		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}	

	} 

	public abstract <R, E> R accept(E env, FnExpVisitor<R, E> v);

	@Override
	public abstract boolean equals(Object o);
/*
	public final Pair<SetExp, SetExp> type(FQLProgram env) {
		return accept(env, new FnExpChecker(
				new LinkedList<String>()));
	} */
	
	public static class Iso extends FnExp {
		
		public Iso(boolean lToR, SetExp l, SetExp r) {
			this.lToR = lToR;
			this.l = l;
			this.r = r;
		}
		@Override
		public String toString() {
			if (lToR) {
				return "iso1 " + l + " " + r;
			} else {
				return "iso2 " + l + " " + r;
			}
		}
		
		boolean lToR;
		SetExp l, r;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((l == null) ? 0 : l.hashCode());
			result = prime * result + (lToR ? 1231 : 1237);
			result = prime * result + ((r == null) ? 0 : r.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Iso other = (Iso) obj;
			if (l == null) {
				if (other.l != null)
					return false;
			} else if (!l.equals(other.l))
				return false;
			if (lToR != other.lToR)
				return false;
			if (r == null) {
				if (other.r != null)
					return false;
			} else if (!r.equals(other.r))
				return false;
			return true;
		}
		
		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}	
	}
	
	public static class Chr extends FnExp {
		public FnExp f;

		public Chr(FnExp f) {
			super();
			this.f = f;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((f == null) ? 0 : f.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Chr other = (Chr) obj;
			if (f == null) {
				if (other.f != null)
					return false;
			} else if (!f.equals(other.f))
				return false;
			return true;
		}
		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}	

	}
	
	public static class Krnl extends FnExp {
		public Krnl(FnExp f) {
			super();
			this.f = f;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((f == null) ? 0 : f.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Krnl other = (Krnl) obj;
			if (f == null) {
				if (other.f != null)
					return false;
			} else if (!f.equals(other.f))
				return false;
			return true;
		}

		public FnExp f;
		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}	

	}
	
	public static class Id extends FnExp {
		public SetExp t;

		public Id(SetExp t) {
			this.t = t;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((t == null) ? 0 : t.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Id other = (Id) obj;
			if (t == null) {
				if (other.t != null)
					return false;
			} else if (!t.equals(other.t))
				return false;
			return true;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

		@Override
		public String toString() {
			return "id " + t;
		}

	}

	
	public static class Const extends FnExp {
		
		FUNCTION<?,?> f;
		SetExp src, dst; 
		
		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

		public Const(FUNCTION<?,?> f, SetExp src, SetExp dst) {
			super();
			this.f = f;
			this.src = src;
			this.dst = dst;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Const other = (Const) obj;
			if (dst == null) {
				if (other.dst != null)
					return false;
			} else if (!dst.equals(other.dst))
				return false;
			if (f == null) {
				if (other.f != null)
					return false;
			} else if (!f.equals(other.f))
				return false;
			if (src == null) {
				if (other.src != null)
					return false;
			} else if (!src.equals(other.src))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((dst == null) ? 0 : dst.hashCode());
			result = prime * result + ((f == null) ? 0 : f.hashCode());
			result = prime * result + ((src == null) ? 0 : src.hashCode());
			return result;
		}

	}

	public static class Var extends FnExp {
		public String v;

		public Var(String v) {
			this.v = v;
			if (v.contains(" ")) {
				throw new RuntimeException();
			}
		}

		@Override
		public String toString() {
			return v;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((v == null) ? 0 : v.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Var other = (Var) obj;
			if (v == null) {
				if (other.v != null)
					return false;
			} else if (!v.equals(other.v))
				return false;
			return true;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}
	
	
	public static class Tru extends FnExp {
		
		String str;
		
		public Tru(String str) {
			super();
			this.str = str;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((str == null) ? 0 : str.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tru other = (Tru) obj;
			if (str == null) {
				if (other.str != null)
					return false;
			} else if (!str.equals(other.str))
				return false;
			return true;
		}
		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}
	}

	public static class TT extends FnExp {
		SetExp t;

		public TT(SetExp t) {
			this.t = t;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((t == null) ? 0 : t.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TT other = (TT) obj;
			if (t == null) {
				if (other.t != null)
					return false;
			} else if (!t.equals(other.t))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "unit " + t;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}

	public static class FF extends FnExp {
		SetExp t;

		public FF(SetExp t) {
			this.t = t;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((t == null) ? 0 : t.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FF other = (FF) obj;
			if (t == null) {
				if (other.t != null)
					return false;
			} else if (!t.equals(other.t))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "void " + t;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}

	public static class Fst extends FnExp {
		SetExp s, t;

		public Fst(SetExp s, SetExp t) {
			this.s = s;
			this.t = t;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((s == null) ? 0 : s.hashCode());
			result = prime * result + ((t == null) ? 0 : t.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Fst other = (Fst) obj;
			if (s == null) {
				if (other.s != null)
					return false;
			} else if (!s.equals(other.s))
				return false;
			if (t == null) {
				if (other.t != null)
					return false;
			} else if (!t.equals(other.t))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "fst " + s + " " + t;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}

	public static class Snd extends FnExp {
		SetExp s, t;

		public Snd(SetExp s, SetExp t) {
			this.s = s;
			this.t = t;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((s == null) ? 0 : s.hashCode());
			result = prime * result + ((t == null) ? 0 : t.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Snd other = (Snd) obj;
			if (s == null) {
				if (other.s != null)
					return false;
			} else if (!s.equals(other.s))
				return false;
			if (t == null) {
				if (other.t != null)
					return false;
			} else if (!t.equals(other.t))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "snd " + s + " " + t;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}

	public static class Inl extends FnExp {
		SetExp s, t;

		public Inl(SetExp s, SetExp t) {
			this.s = s;
			this.t = t;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((s == null) ? 0 : s.hashCode());
			result = prime * result + ((t == null) ? 0 : t.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Inl other = (Inl) obj;
			if (s == null) {
				if (other.s != null)
					return false;
			} else if (!s.equals(other.s))
				return false;
			if (t == null) {
				if (other.t != null)
					return false;
			} else if (!t.equals(other.t))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "inl " + s + " " + t;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}

	public static class Inr extends FnExp {
		SetExp s, t;

		public Inr(SetExp s, SetExp t) {
			this.s = s;
			this.t = t;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((s == null) ? 0 : s.hashCode());
			result = prime * result + ((t == null) ? 0 : t.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Inr other = (Inr) obj;
			if (s == null) {
				if (other.s != null)
					return false;
			} else if (!s.equals(other.s))
				return false;
			if (t == null) {
				if (other.t != null)
					return false;
			} else if (!t.equals(other.t))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "inr " + s + " " + t;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}

	public static class Eval extends FnExp {
		SetExp s, t;

		public Eval(SetExp s, SetExp t) {
			this.s = s;
			this.t = t;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((s == null) ? 0 : s.hashCode());
			result = prime * result + ((t == null) ? 0 : t.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Eval other = (Eval) obj;
			if (s == null) {
				if (other.s != null)
					return false;
			} else if (!s.equals(other.s))
				return false;
			if (t == null) {
				if (other.t != null)
					return false;
			} else if (!t.equals(other.t))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "eval " + s + " " + t;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}

	public static class Curry extends FnExp {
		FnExp f;

		public Curry(FnExp f) {
			this.f = f;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((f == null) ? 0 : f.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Curry other = (Curry) obj;
			if (f == null) {
				if (other.f != null)
					return false;
			} else if (!f.equals(other.f))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "curry " + f;
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}

	public static class Prod extends FnExp {
		FnExp l, r;

		public Prod(FnExp l, FnExp r) {
			this.l = l;
			this.r = r;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((l == null) ? 0 : l.hashCode());
			result = prime * result + ((r == null) ? 0 : r.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Prod other = (Prod) obj;
			if (l == null) {
				if (other.l != null)
					return false;
			} else if (!l.equals(other.l))
				return false;
			if (r == null) {
				if (other.r != null)
					return false;
			} else if (!r.equals(other.r))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "(" + l + " * " + r + ")";
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}

	public static class Comp extends FnExp {
		FnExp l, r;

		public Comp(FnExp FnExp, FnExp r) {
			this.l = FnExp;
			this.r = r;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((l == null) ? 0 : l.hashCode());
			result = prime * result + ((r == null) ? 0 : r.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Comp other = (Comp) obj;
			if (l == null) {
				if (other.l != null)
					return false;
			} else if (!l.equals(other.l))
				return false;
			if (r == null) {
				if (other.r != null)
					return false;
			} else if (!r.equals(other.r))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "(" + l + " ; " + r + ")";
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}

	public static class Case extends FnExp {
		FnExp l, r;

		public Case(FnExp l, FnExp r) {
			this.l = l;
			this.r = r;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((l == null) ? 0 : l.hashCode());
			result = prime * result + ((r == null) ? 0 : r.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Prod other = (Prod) obj;
			if (l == null) {
				if (other.l != null)
					return false;
			} else if (!l.equals(other.l))
				return false;
			if (r == null) {
				if (other.r != null)
					return false;
			} else if (!r.equals(other.r))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "(" + l + " + " + r + ")";
		}

		@Override
		public <R, E> R accept(E env, FnExpVisitor<R, E> v) {
			return v.visit(env, this);
		}

	}
	
	@Override
	public abstract int hashCode();

	public interface FnExpVisitor<R, E> {
		public R visit(E env, Id e);
		public R visit(E env, Comp e);
		public R visit(E env, Var e);
		public R visit(E env, Const e);
		public R visit(E env, TT e);
		public R visit(E env, FF e);
		public R visit(E env, Fst e);
		public R visit(E env, Snd e);
		public R visit(E env, Inl e);
		public R visit(E env, Inr e);
		public R visit(E env, Eval e);
		public R visit(E env, Curry e);
		public R visit(E env, Case e);
		public R visit(E env, Prod e);
		public R visit(E env, Iso e);
		public R visit(E env, Chr e);
		public R visit(E env, Krnl e);
		public R visit(E env, Tru e);
		public R visit(E env, Apply e);
		public R visit(E env, ApplyTrans e);
	}

}
